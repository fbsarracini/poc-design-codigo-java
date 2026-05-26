# poc-design-codigo-java

> 🇧🇷 [Leia em português](./README.pt-br.md)

A POC for exploring **code design oriented towards Clean Architecture in Java**, using Spring Boot as the runtime platform but without letting the framework dictate the domain boundaries.

The goal is not to apply Uncle Bob's diagram literally (concentric layers, named *ports & adapters*, etc.), but to practice the principles behind it:

- **Dependencies point inward, towards the domain** — never the other way around.
- **Business rules don't know about transport** (HTTP, JSON, JPA) — controllers, requests/responses and repositories are details.
- **Use cases are explicit units** (`CreateNewAccount`, `CreateNewUser`, …), not methods scattered across a generic *service*.
- **Domain exceptions** (`IllegalArgumentException` thrown from the core) are translated at the HTTP boundary — the domain doesn't import `@ResponseStatus`.

## Code organization

Instead of packages by layer (`controller/`, `service/`, `repository/`), the project uses **package by feature** — each vertical slice lives together:

```
io.github.fbsarracini.javadesign
├── account/   Account, CreateNewAccount, NewAccountController, NewAccount{Request,Response,Data}, AccountRepository
├── user/      User,    CreateNewUser,    NewUserController,    NewUser{Request,Response,Data},    UserRepository
├── invite/
├── auth/
├── config/    Spring bootstrap (security, beans, etc.)
└── exception/ translation of domain exceptions into HTTP responses
```

Cohesion stays within the feature package; coupling between features is explicit and minimal.

## Stack

- Java 25 + Spring Boot 4
- Spring Web MVC, Spring Security, Spring Data JPA, Bean Validation
- PostgreSQL + Flyway (versioned migrations)
- JWT (jjwt) for authentication
- springdoc-openapi for API documentation
- Multi-stage Docker build with *layered jar* and *CDS archive* for faster startup

## Running it

```bash
docker compose up --build
```

The application starts on `http://localhost:8080` (Swagger UI at `/swagger-ui.html`) and Postgres on `localhost:5432`. Port `5005` is exposed by compose for *remote debug* (JDWP).

Relevant variables (all have defaults in `docker-compose.yml`):

- `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (required — no default)
- `JWT_EXPIRATION`
- `APP_UID` / `APP_GID` — pass `$(id -u)` / `$(id -g)` to avoid permission conflicts on bind-mounts


## Screenshots
<img width="1807" height="861" alt="2" src="https://github.com/user-attachments/assets/c82fd38c-76b2-435f-a759-6de4147f03a3" />

<img width="1826" height="850" alt="3" src="https://github.com/user-attachments/assets/002c31c9-f3e9-4ff2-89f7-c2765004bf4a" />

<img width="1881" height="856" alt="4" src="https://github.com/user-attachments/assets/7e87a228-0c99-4084-8150-b3da22454c8e" />

<img width="1878" height="858" alt="5" src="https://github.com/user-attachments/assets/6634b55e-ccf3-4407-a5a3-79c0193bf2af" />


