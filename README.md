# 2idiotz

A small, working foundation for a web app. The homepage welcomes visitors; `/status` checks the application and database. There are no business features or user accounts yet.

## Start the whole app

You need Docker with Compose. From the repository root:

```sh
cp .env.example .env
docker compose up --build -d
```

Open **http://localhost:8080**. To stop it, run `docker compose down`. Local PostgreSQL data is kept in a named volume; `docker compose down -v` deletes that data.

## Work on the code

Use Java 21, Node 22.22.2 or newer, and pnpm 10.34.5. The Gradle Wrapper is included; you do not need to install Gradle.

To install pnpm once:

```sh
npm install --global pnpm@10.34.5
```

For frontend development:

```sh
cd frontend
pnpm install --frozen-lockfile
pnpm dev
```

Vite forwards `/api` requests to a backend on port 8080. To run that backend separately, first start PostgreSQL from the repository root in another terminal:

```sh
docker compose run --rm -p 127.0.0.1:5432:5432 postgres
```

Then set `POSTGRES_PASSWORD` in your shell to the value from `.env` and run:

```sh
cd backend
./gradlew :app:bootRun
```

The database name and user default to `app`. Set `DATABASE_URL` and `POSTGRES_USER` if needed. Don’t run the full Compose stack at the same time: Caddy also needs host port 8080.

## Everyday checks

| Where | Command | What it does |
| --- | --- | --- |
| `frontend` | `pnpm check` | Checks Svelte and TypeScript |
| `frontend` | `pnpm lint` | Checks lint rules and formatting |
| `frontend` | `pnpm format` | Formats source files |
| `frontend` | `pnpm test` | Tests API validation, failures, cancellation, and timeout handling |
| `frontend` | `pnpm test:e2e` | Builds the app and runs desktop/mobile browser tests |
| `backend` | `./gradlew test` | Runs unit and architecture tests without Docker |
| `backend` | `./gradlew check` | Runs all checks, including PostgreSQL integration tests; requires Docker |
| `backend` | `./gradlew spotlessApply` | Formats Java and module build files |
| `backend` | `./gradlew :app:bootJar` | Builds the runnable application |
| Repository root | `python3 scripts/smoke.py` | Checks a running Compose stack, including database outage and recovery |

Install Chromium before the first browser run: `pnpm exec playwright install chromium`. CI also installs its system dependencies. Browser tests mock API responses to exercise UI failures reliably; backend integration tests and the Compose smoke check use real PostgreSQL.

## How the code is organized

### Frontend

SvelteKit and TypeScript, with shadcn-svelte components built on Bits UI, Tailwind CSS, and Lucide icons.

- `src/routes`: page composition and navigation.
- `src/lib/components/ui`: editable shadcn components. Add components with `pnpm dlx shadcn-svelte@latest add <name>`.
- `src/lib/api`: HTTP requests and Zod response schemas. Types come from the schemas.
- `src/lib/features/status`: status behavior and presentation.
- `src/app.css`: shared theme tokens and base styles.

Use native fetch through the API client. Requests have a timeout, validate responses, and accept cancellation. Pages discard results after navigation or a newer request. The status page checks on mount and on request, without background polling.

### Backend

Three Gradle modules, with feature packages inside each:

| Module | Owns | May depend on |
| --- | --- | --- |
| `domain` | Plain Java health model, service, and database-check interface | Java only in production |
| `infrastructure` | JDBC implementation and database wiring | `domain`, Spring JDBC |
| `app` | HTTP controllers, response records, Spring Boot entrypoint, composition | `domain`, `infrastructure` |

Controllers map HTTP requests and responses. Infrastructure checks PostgreSQL with `SELECT 1` and translates database failures into an availability result. Only application configuration imports infrastructure directly; ArchUnit protects the boundaries.

Dependency versions live in `backend/gradle/libs.versions.toml`. The convention plugin in `backend/build-logic` shares Java 21, JUnit, and Spotless settings. Only `app` applies the Spring Boot plugin and produces an executable JAR. Integration tests are tagged separately and included in `check`, not `test`.

### API contract

| Endpoint | Response |
| --- | --- |
| `GET /api/hello` | `200 {"message":"Hello from Spring Boot!"}` |
| `GET /api/health` | `200 {"status":"UP","database":"UP"}` |
| `GET /api/health` with PostgreSQL unavailable | `503 {"status":"DOWN","database":"DOWN"}` |

Health responses use `Cache-Control: no-store` and do not expose connection details. The backend can start before PostgreSQL and recover when it becomes available.

## Deployment

The public repository is [NightmarePog/2idiotz](https://github.com/NightmarePog/2idiotz). Tour de Cloud team: `app_build_repl_idiot`. Its required collaborator is `Tour-de-App-user`.

In repository **Settings → Secrets and variables → Actions**, configure:

| Kind | Name | Value |
| --- | --- | --- |
| Secret | `TDC_TOKEN` | Token from Tour de Cloud’s **Přístupové klíče**; enter it directly in GitHub |
| Secret | `POSTGRES_PASSWORD` | Database password; already generated for this repository |
| Variable | `APP_ORIGIN` | Public HTTPS application origin, without a trailing slash |

Push to `main` or manually run **Check and upload to Tour de Cloud**. Pull requests run checks without uploading. CI runs backend checks with Testcontainers, frontend checks and Playwright, then production-image smoke tests before upload. Docker image builds do not need Docker-in-Docker.

After a successful upload, open **Verze** in Tour de Cloud, select the uploaded commit, and click **Nasadit**. Uploading alone does not deploy it.

Caddy is the only public container, on port 80. It forwards `/api` and `/api/*` to Spring Boot and everything else to SvelteKit. Containers communicate through `localhost` in Tour de Cloud and service names in local Compose. Credentials are runtime configuration, never frontend data or Docker build arguments.

**Cloud PostgreSQL is disposable.** This deployment does not request persistent storage. Data may disappear when the pod is replaced or redeployed. Use persistent storage or an external database before storing valuable data.
