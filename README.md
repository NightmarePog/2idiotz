# 2idiotz

A SvelteKit + Java Spring Boot starter, with PostgreSQL and Caddy, configured for Tour de Cloud team `app_build_repl_idiot`.

## Run locally

Install Docker with Compose, then:

```sh
cp .env.example .env
docker compose up --build -d
```

Open **http://localhost:8080**. Caddy routes `/api` and `/api/*` to Spring Boot; everything else goes to SvelteKit. Only Caddy is published to the host.

```sh
python3 scripts/smoke.py
docker compose down
```

The smoke check stops and restarts the local PostgreSQL container to test recovery. Local database data survives `docker compose down`; `docker compose down -v` deletes it.

## Develop and check

The frontend uses SvelteKit, TypeScript, and adapter-node. Use Node 22.22.2+:

```sh
cd frontend
npm ci
npm run check
npm run build
npm run dev
```

Vite forwards `/api` to a backend running on host port 8080. The backend uses Gradle Kotlin DSL and a checksum-verified Gradle 8.14.3 Wrapper. For separate backend development, provide PostgreSQL on host port 5432 and set `POSTGRES_PASSWORD`, then use Java 21:

```sh
cd backend
./gradlew test
./gradlew bootRun
```

To run only the local Compose database and publish it for backend development:

```sh
docker compose run --rm -p 127.0.0.1:5432:5432 postgres
```

Do not run the full Compose stack at the same time as a backend listening on host port 8080. The default database/user are both `app`. Override `DATABASE_URL`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` when needed. Backend tests do not require a database; the full-stack smoke check uses real PostgreSQL.

## API

| Endpoint | Response |
| --- | --- |
| `GET /api/hello` | `200 {"message":"Hello from Spring Boot!"}` |
| `GET /api/health` | `200 {"status":"UP","database":"UP"}` after `SELECT 1` succeeds |
| `GET /api/health` during a database outage | `503 {"status":"DOWN","database":"DOWN"}` |

The API starts even when PostgreSQL is unavailable and reconnects when it returns. No application tables, login, or user data are included.

## Deploy to Tour de Cloud

1. Create the public GitHub repository `NightmarePog/2idiotz` and invite **Tour-de-App-user** as a collaborator.
2. In GitHub **Settings → Secrets and variables → Actions**, add repository secrets:
   - `TDC_TOKEN`: create this in Tour de Cloud's **Přístupové klíče**. Enter it directly in GitHub; never commit it or share it in chat.
   - `POSTGRES_PASSWORD`: generate a strong database password and enter it directly in GitHub.
3. Add repository **variable** `APP_ORIGIN`, the public application origin shown by Tour de Cloud, such as `https://your-app.example`, without a trailing slash. This sets SvelteKit's canonical origin behind the platform's HTTPS proxy.
4. Push to `main`, or run **Check and upload to Tour de Cloud** manually on `main`. Pull requests run checks without uploading.
5. Once the workflow passes, open **Verze** in Tour de Cloud, select the uploaded commit, and click **Nasadit**. Uploading alone does not deploy the version.
6. Open the application link and verify both service indicators say **Connected**.

The workflow builds the production images, checks frontend types, runs backend tests, and exercises Caddy plus a real PostgreSQL outage/recovery before uploading. Missing deployment settings fail with instructions, without printing secrets.

`tourdeapp.yaml` exposes port 80 only. The four containers share `localhost` in Tour de Cloud; local Compose uses service names instead. Team images are untagged in the manifest so the upload Action can assign the commit hash. The public PostgreSQL image uses the explicit `17-alpine` tag.

**Cloud PostgreSQL is disposable.** This configuration does not request persistent storage. Data can disappear when the pod is replaced or the application is redeployed. Use persistent platform storage or an external database before storing valuable data.

Keep `.env` out of Git. Database credentials are injected into runtime configuration, never bundled into the frontend or passed as image build arguments.
