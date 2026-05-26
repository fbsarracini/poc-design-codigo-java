.DEFAULT_GOAL := help
.PHONY: help setup build run test clean db-up db-down db-logs up down logs ps rebuild swagger

MVN := ./mvnw
COMPOSE := docker compose

# UID/GID do host repassados para o container (evita conflito de permissão
# em bind-mounts). Exportados como env para o docker compose ler nos build args.
export APP_UID ?= $(shell id -u)
export APP_GID ?= $(shell id -g)

help: ## Lista os comandos disponíveis
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'

setup: ## Copia application.properties.sample se ainda não existir
	@test -f src/main/resources/application.properties || cp src/main/resources/application.properties.sample src/main/resources/application.properties
	@echo "application.properties pronto."

build: ## Compila o projeto (sem testes)
	$(MVN) -DskipTests package

test: ## Roda a suíte de testes
	$(MVN) test

clean: ## Limpa artefatos de build
	$(MVN) clean

db-up: ## Sobe apenas o Postgres
	$(COMPOSE) up -d db

db-down: ## Para o Postgres
	$(COMPOSE) stop db

db-logs: ## Tail dos logs do Postgres
	$(COMPOSE) logs -f db

run: setup db-up ## Sobe Postgres e roda a app local (Spring Boot)
	$(MVN) spring-boot:run

up: ## Sobe tudo via docker compose (db + app)
	$(COMPOSE) up -d --build

down: ## Derruba tudo (mantém volumes)
	$(COMPOSE) down

rebuild: ## Rebuilda a imagem da app e reinicia
	$(COMPOSE) up -d --build app

logs: ## Tail dos logs da app
	$(COMPOSE) logs -f app

ps: ## Status dos containers
	$(COMPOSE) ps

swagger: ## Abre o Swagger UI no navegador
	@xdg-open http://localhost:8080/swagger-ui.html >/dev/null 2>&1 || echo "Abra: http://localhost:8080/swagger-ui.html"
