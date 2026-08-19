# User Management

A responsive full-stack user management application built with Angular Reactive Forms, NgRx,
Bootstrap, Java 26, Spring Boot, H2, Flyway, Gradle, and Docker Compose.

## Architecture

The application runs as three separate containers:

- `frontend`: Angular production build served by Nginx; Nginx proxies `/api` to the backend.
- `backend`: Spring Boot REST API running on Java 26; Flyway owns database migrations.
- `database`: H2 running in TCP server mode on Java 26 with a persistent Docker volume.

## Run the application

Requirements: Docker with Docker Compose.

```bash
docker compose up --build
```

Open <http://localhost:4200>.

To stop the services:

```bash
docker compose down
```

To also remove the persisted H2 data:

```bash
docker compose down --volumes
```

## Run tests locally

Backend tests require JDK 26. No global Gradle installation is needed:

```bash
./gradlew :backend:test
```

Frontend tests require a supported Node.js version (Node 24 recommended):

```bash
cd frontend
npm ci
npm run test:ci
```

Run both suites from the repository root:

```bash
./gradlew check
```

## REST API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/users` | Create a user |
| `GET` | `/api/users` | Get all users |
| `PUT` | `/api/users/{id}` | Update a user |

Example request:

```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com"
}
```

All runtime exceptions are converted to sanitized JSON responses by the backend's
`@RestControllerAdvice`. Validation failures include field-level messages.

## Project notes

- The runtime H2 database is separate from the backend and communicates over TCP.
- Tests use an isolated in-memory H2 database and run the same Flyway migration.
- Hibernate uses `ddl-auto: validate`; it never creates or updates the schema.
- The responsive UI uses Bootstrap's mobile-first grid and a responsive table wrapper.
- Email addresses are validated but are not required to be unique by the assignment.

