# User Management

A responsive user management application built with Angular 21, Angular Reactive Forms,
NgRx, Bootstrap 5, Java 26, Spring Boot 4, H2, Flyway, Lombok, Gradle, and Docker Compose.

Users can be created, listed, edited, and deleted. All fields are required, email addresses are
validated and normalized to lowercase, and registered email addresses must be unique.

## Architecture

The application runs as three separate containers:

- `frontend`: an Angular production build served by Nginx. Nginx forwards `/api` requests
  to the backend.
- `backend`: the Spring Boot REST API running on Java 26. Flyway manages its database
  schema and Hibernate validates that schema.
- `database`: H2 running in TCP server mode on Java 26 with data stored in a Docker volume.

The frontend uses route-level components under `users/pages`. User data, loading state,
saving state, and errors are managed by NgRx Store and Effects. Angular Reactive Forms
manage create and edit form state and validation.

```text
.
├── backend/
│   ├── src/main/java/com/example/usermanagement/
│   │   ├── api/              # REST controller and request/response models
│   │   ├── entity/           # JPA entities
│   │   ├── exception/        # Exceptions and global REST exception handling
│   │   ├── repository/       # Spring Data repositories
│   │   └── service/          # Service interface and implementation
│   ├── src/main/resources/db/migration/
│   └── src/test/             # Service integration and controller tests
├── database/                 # Standalone H2 TCP container
├── frontend/
│   └── src/app/
│       ├── core/api/         # Backend API client
│       └── users/
│           ├── models/
│           ├── pages/        # Home, user form, and user list route components
│           └── store/        # NgRx actions, effects, reducer, selectors, and state
├── compose.yaml              # Production-style containers
└── compose.dev.yaml          # Local development containers
```

## Run with Docker

Docker with Docker Compose is required.

```bash
docker compose up --build
```

Open <http://localhost:4200>.

Stop the application while retaining database data:

```bash
docker compose down
```

Remove the containers and persisted database data:

```bash
docker compose down --volumes
```

## Development mode

```bash
docker compose -f compose.dev.yaml up --build
```

Open the frontend at <http://localhost:4200>. The backend API is exposed directly at
<http://localhost:8080/api/users> in development mode.

Changes under `frontend/src` and `frontend/public` trigger Angular recompilation and browser
refresh. Backend source is also mounted into its container, but Java changes require a
backend restart:

```bash
docker compose -f compose.dev.yaml restart backend
```

Rebuild the relevant development image after changing dependencies, build files, Dockerfiles,
or other container configuration.

Stop the development environment while retaining its database and caches:

```bash
docker compose -f compose.dev.yaml down
```

Add `--volumes` to remove the development database and dependency caches as well.

### Regular and development mode differences

| Area | Regular mode (`compose.yaml`) | Development mode (`compose.dev.yaml`) |
| --- | --- | --- |
| Intended use | Running the complete application as packaged production-style images | Editing and testing the application locally |
| Frontend | Angular is compiled during the image build and the static files are served by Nginx on container port `80` | Angular CLI development server runs on container port `4200` |
| Frontend updates | Source changes require rebuilding the frontend image | Changes under `frontend/src` and `frontend/public` are watched and recompiled automatically |
| API proxy | Nginx forwards `/api` to the backend | Angular's development proxy forwards `/api` to the backend |
| Backend | A Spring Boot executable JAR is built into a Java runtime image | Gradle runs the backend directly with `bootRun` in a JDK image |
| Backend updates | Java changes require rebuilding the backend image | `backend/src` is mounted into the container; restart the backend container to recompile changes |
| Host ports | Only the frontend is published at `4200`; backend and database remain inside the Compose network | Frontend is published at `4200` and backend at `8080`; the database remains internal |
| Persistence | H2 data is stored in the `h2-data` volume | H2 data is stored separately in `h2-dev-data`; Gradle and Angular caches also use development volumes |
| Container lifecycle | Services use automatic restart policies | Services are left stopped after they exit so development failures remain visible |

The two modes use separate H2 volumes, so users created in one mode are not visible in the
other. In either mode, changing dependencies or container configuration requires rebuilding
the affected image with `--build`.

### Remove H2 data

Database removal is permanent. Each mode has its own named H2 volume and must be cleared with
the corresponding Compose file.

Remove regular-mode H2 data from `h2-data`:

```bash
docker compose down --volumes
```

Remove development-mode H2 data from `h2-dev-data`:

```bash
docker compose -f compose.dev.yaml down --volumes
```

The development command also removes the Gradle and Angular cache volumes declared in
`compose.dev.yaml`. To remove H2 data from both modes, run both commands. The databases are
created again as empty databases the next time their respective environments start.

`docker system prune -a --volumes` is not a substitute for these commands: Docker's system
prune removes anonymous volumes, while this project deliberately uses named volumes.

## REST API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/users` | Create a user |
| `GET` | `/api/users` | Get all users |
| `PUT` | `/api/users/{id}` | Update a user |
| `DELETE` | `/api/users/{id}` | Delete a user |

Example create or update request:

```json
{
  "firstName": "Ada",
  "lastName": "Lovelace",
  "email": "ada@example.com"
}
```

The backend returns field-level validation errors with `400 Bad Request`, missing users with
`404 Not Found`, and duplicate email addresses with `409 Conflict`. A global
`@RestControllerAdvice` converts other runtime exceptions to sanitized JSON responses.

## Tests

Backend tests require JDK 26. The Gradle wrapper is included:

```bash
./gradlew :backend:test
```

The backend suite includes a service integration test against an isolated in-memory H2
database with Flyway migrations and a controller test with a mocked `UserService`.

Frontend tests require Node.js `20.19` through `24`; Node.js 24 is used by the frontend
containers.

```bash
cd frontend
npm ci
npm run test:ci
```

The frontend suite covers Reactive Form validation, the NgRx reducer, and responsive user-list
markup.

Run both backend and frontend suites from the repository root (JDK, Node.js, and npm required):

```bash
./gradlew check
```
