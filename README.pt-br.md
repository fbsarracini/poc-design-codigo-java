# poc-design-codigo-java

> 🇺🇸 [Read in English](./README.md)

POC para explorar **design de código orientado a Clean Architecture em Java**, usando Spring Boot como plataforma de runtime mas sem deixar o framework ditar as fronteiras do domínio.

A ideia não é aplicar o diagrama do Uncle Bob ao pé da letra (camadas concêntricas, *ports & adapters* nomeados, etc.), e sim praticar os princípios que estão por trás dele:

- **Dependências apontam para o domínio**, nunca o contrário.
- **Regra de negócio não conhece o transporte** (HTTP, JSON, JPA) — controllers, requests/responses e repositórios são detalhes.
- **Casos de uso são unidades explícitas** (`CreateNewAccount`, `CreateNewUser`, …), não métodos perdidos em um *service* genérico.
- **Exceções de domínio** (`IllegalArgumentException` vinda do core) são traduzidas na borda HTTP — o domínio não importa `@ResponseStatus`.

## Organização do código

Em vez de pacotes por camada (`controller/`, `service/`, `repository/`), o projeto usa **package by feature** — cada *slice* vertical mora junto:

```
io.github.fbsarracini.javadesign
├── account/   Account, CreateNewAccount, NewAccountController, NewAccount{Request,Response,Data}, AccountRepository
├── user/      User,    CreateNewUser,    NewUserController,    NewUser{Request,Response,Data},    UserRepository
├── invite/
├── auth/
├── config/    bootstrap do Spring (security, beans, etc.)
└── exception/ tradução de exceções de domínio para respostas HTTP
```

A coesão fica dentro do pacote da feature; o acoplamento entre features é explícito e mínimo.

## Stack

- Java 25 + Spring Boot 4
- Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation
- PostgreSQL + Flyway (migrations versionadas)
- JWT (jjwt) para autenticação
- springdoc-openapi para documentação da API
- Docker multi-stage com *layered jar* e *CDS archive* para start-up rápido

## Como rodar

```bash
docker compose up --build
```

A aplicação sobe em `http://localhost:8080` (Swagger UI em `/swagger-ui.html`) e o Postgres em `localhost:5432`. A porta `5005` é exposta pelo compose para *remote debug* (JDWP).

Variáveis relevantes (todas têm default no `docker-compose.yml`):

- `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (obrigatória — sem default)
- `JWT_EXPIRATION`
- `APP_UID` / `APP_GID` — passe `$(id -u)` / `$(id -g)` para evitar conflito de permissão em bind-mounts
