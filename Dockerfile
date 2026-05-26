# syntax=docker/dockerfile:1.7

############################
# 1) Build
############################
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace

# Resolve dependências primeiro (camada cacheável separada do código)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -DskipTests dependency:go-offline

# Build do JAR
COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -DskipTests package

# Extrai o layered JAR do Spring Boot para camadas Docker independentes
RUN java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination extracted

# CDS training run: sobe o contexto, dispara dump e sai (sem precisar de DB).
# Vars desligam DB/Flyway pra training run rodar offline.
# DATABASE_PLATFORM evita que o Hibernate conecte ao DB pra detectar o dialect.
RUN timeout 120 java \
    -Dspring.context.exit=onRefresh \
    -Dspring.autoconfigure.exclude=\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,\
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration \
    -Djwt.secret=cds-training-secret-must-have-at-least-256-bits \
    -XX:ArchiveClassesAtExit=/workspace/extracted/application/app.jsa \
    -jar target/*.jar \
    || true \
    && test -f /workspace/extracted/application/app.jsa

############################
# 2) Runtime
############################
FROM eclipse-temurin:25-jre-alpine

# Usuário não-root com UID/GID configuráveis (passe do host para evitar
# conflito de permissão em bind-mounts: --build-arg APP_UID=$(id -u) ...)
ARG APP_UID=1000
ARG APP_GID=1000
RUN addgroup -g ${APP_GID} -S app && adduser -u ${APP_UID} -S -G app app
USER app
WORKDIR /app

# Copia camadas da mais estável (deps) para a menos estável (código + CDS archive)
COPY --from=build --chown=app:app /workspace/extracted/dependencies/ ./
COPY --from=build --chown=app:app /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=app:app /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=app:app /workspace/extracted/application/ ./

EXPOSE 8080

# JDK_JAVA_OPTIONS é lido automaticamente pelo launcher — sem precisar de sh.
# Container-aware: 75% da RAM do container, G1, fail-fast em OOM, usa CDS.
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -XX:SharedArchiveFile=/app/app.jsa"

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
