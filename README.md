# Think different Academy

Minimal SvelteKit frontend and Spring Boot backend.

All new or modified UI must follow the [Think different Academy brand guide](docs/brand/README.md). The official manual is included locally, and frontend instructions record this requirement.

The frontend uses Tailwind CSS and shadcn-svelte. Page layout uses utility classes; `src/app.css` contains the shared theme. Add components as needed with `pnpm exec shadcn-svelte add <component>` from `frontend`.

The interface defaults to dark mode; the header toggle saves a light/dark preference locally before the next page paints. The homepage is a concise landing page with one link to the stop finder and an API-provided stop photo. Search and amenity filters live on `/stops`. Results use compact photo rows; supporting browsers transition the selected stop image into its detail view. All motion respects reduced-motion preferences. Search (`q`) and combined amenity filters (`filter=accessible,shelter,tickets`) are shareable URL state and survive detail navigation.

Stop images use responsive WebP derivatives in `frontend/static/images/stops`, with the original API image as a fallback. To regenerate them from the unchanged backend seeds, install Pillow in a Python environment and run `python3 frontend/scripts/optimize-stop-images.py` from the repository root.

Frontend code follows three layers:

- `frontend/src/lib/ui`: shadcn-svelte primitives (Button, Card, Badge, Empty, Spinner). `components.json` directs the CLI here. Shared brand and touch-target adjustments belong in these primitives and the CSS theme.
- `frontend/src/lib/components`: domain-independent compositions such as AppShell, PageState, and LoadingState, built from UI primitives. These do not fetch API data or import features.
- `frontend/src/lib/features`: page behavior and domain-specific components, grouped by feature (`stops`, `home`). Features own API calls, loading/error state, and feature presentation; use the generated SDK directly.

SvelteKit routes only compose features and pass route parameters. Keep stop-specific cards, images, and details inside the stops feature. The dependency direction is routes → features/shared components → UI; UI and shared components must not import features or the API client. Do not edit generated API files when refactoring UI.


- `/` displays **Think different Academy**, calls the health endpoint, and shows **Status: OK**.
- `GET /api/v1/team` reads the team name and ordered member names from PostgreSQL; the homepage displays them in its authors footer.
- `GET /api/v1/health` returns `200 {"status":"ok"}`.

## Run with Docker

```sh
cp .env.example .env
# Choose a database password in .env.
docker compose up --build -d
```

Open http://localhost:8080. Stop with `docker compose down`. PostgreSQL data is stored in the `postgres_data` volume and survives container recreation. `docker compose down -v` deletes that data.

## Development

Frontend-only development needs Node 22.22.2 or newer and pnpm 10.34.5:

```sh
cd frontend
pnpm install --frozen-lockfile
pnpm dev
```

This starts Faker-backed development without a database or Java. Run `pnpm test:dev` to verify the standalone API, images, and stop mutations.

For development against the real backend, also use Java 21 and Python 3:

Use Python's standard library for automation involving files or JSON, and shell for short command sequences. Keep tool configuration and application tests in their native formats; do not add handwritten JavaScript automation scripts.

```sh
docker compose up -d postgres
set -a; . ./.env; set +a
cd backend
./gradlew bootRun
```

In another terminal:

```sh
cd frontend
pnpm install --frozen-lockfile
pnpm dev:backend
```

`pnpm dev` runs independently with a seeded Faker API in Vite: no Spring or PostgreSQL is required. It serves stop data, team data, health, images, logo, and font through `/api/v1/**`. Images and brand files are read from backend resources on disk; no backend process is started. Stop edits live in memory and reset when Vite restarts.

To use the real backend instead, run `pnpm dev:backend`; Vite then forwards `/api` to Spring on port 8080. Production always uses the real backend.

The backend sets `/api/v1` globally with `server.servlet.context-path` in `backend/src/main/resources/application.yaml`. Controllers declare only their resource path, such as `@GetMapping("/health")`. Both Caddy and Vite preserve the full request path when proxying to the backend.

## Checks

Backend tests and OpenAPI generation need a **separate disposable PostgreSQL database** (`app_test`, default port 5433, user `app`, password `ci-disposable-password`). Configure `TEST_DATABASE_URL`, `TEST_POSTGRES_USER`, and `TEST_POSTGRES_PASSWORD` to override it. Never point these at production: integration tests write test data. CI provides this database automatically.

In `backend`, run `./gradlew check` (formatting, HTTP contracts, and committed JPA writes read through the HTTP API).
In `frontend`, run `pnpm check`, `pnpm lint`, and `pnpm test` (desktop/mobile browser tests).
Install the browser once with `pnpm exec playwright install chromium`.
Run `python3 scripts/smoke.py` against a running Docker Compose stack to check the homepage, assets, and API routing.

## Typed API workflow

Java controllers and DTOs are the source of truth. Springdoc exports OpenAPI; Hey API generates the TypeScript SDK and Zod validators. The homepage calls `getHealth()` from `src/lib/api/generated`, with runtime validation enabled. The contract explicitly requires `status: "ok"`.

After changing a controller or DTO, run from `frontend`:

```sh
pnpm api:generate
```

This exports the contract through the existing Gradle task and generates the client. The export task uses a temporary Spring test server on a random port; no separately running backend is needed, but the test PostgreSQL database described above must be available. Commit `frontend/openapi.json` and the entire `frontend/src/lib/api/generated` directory with your Java changes; never edit generated files by hand.

To verify without changing those files:

```sh
pnpm api:check
```

This generates into a temporary directory and uses `diff` to reject stale, missing, or extra generated files without rewriting the working copy. Both commands use the short shell script `frontend/scripts/api.sh`, the existing Gradle export task, Prettier, and Hey API. They require a POSIX shell and standard utilities (`mktemp`, `diff`, `cp`, `rm`), Java 21, and the installed frontend dependencies; Windows developers can use WSL. No additional task runner or Python package is needed.

Tests and type checking run separately, in this order:

```sh
pnpm api:check
pnpm test:api
pnpm check
```

`test:api` validates the exported live backend response through the generated SDK and tests invalid responses. Run `api:generate` or `api:check` first to produce that response fixture. CI runs all three commands. The lockfile fixes the installed generator and validator versions. Generated code is excluded from formatting/lint rules, but remains type-checked. Application code uses the generated SDK; lint rejects direct global `fetch` calls in `src`.

Use the composed `@ApiGet`, `@ApiPost`, `@ApiPut`, `@ApiPatch`, and `@ApiDelete` annotations for endpoints. Each combines HTTP routing, JSON media types where applicable, the success status, and OpenAPI metadata. Defaults are GET/PUT/PATCH 200, POST 201, and DELETE 204. Set `summary`, `description`, and `responseDescription` on the same annotation; do not add separate `@Operation`, mapping, or `@ResponseStatus` annotations. Springdoc derives operation IDs from unique method names; `operationId` can override one when needed. Transaction boundaries belong in services with `@Transactional`; controllers handle HTTP routing, request validation, headers, and responses.

Apply `@SnakeCase` to API records that need snake_case JSON names, keeping Java fields camelCase. It bundles Jackson 3 naming for runtime JSON and Jackson 2 naming for Springdoc, so the generated schema follows the same convention. Records without it retain the default naming. Use `@JsonProperty` only for exceptions or explicit enum values; validation stays explicit on each field.

Use concrete DTOs and standard Jakarta validation constraints (`@NotNull`, `@NotBlank`, `@Size`, etc.) for required fields and restrictions. Use Java enums for finite allowed values; `@JsonProperty` can give enum constants their JSON spelling. Springdoc derives the schema from these types and constraints, so routine fields do not need `@Schema`. Health uses `@NotNull Status status` with `Status.OK` serialized as `"ok"`. Constraints are enforced when validation is invoked (for example, `@Valid` on a request body); they do not automatically reject null values passed to record constructors. Reserve `@Schema` for documentation or details that cannot be inferred. Add contract coverage for new responses; live contract coverage includes health and the database-backed team endpoint.

Station responses use primitive IDs/booleans and class-level `@Schema(requiredProperties = ...)` to describe required JSON keys. The image key is required but accepts null. Keep boxed booleans with `@NotNull` on requests so missing/null input is rejected. OpenAPI describes Java `long` IDs as `integer/int64`; the Zod generator configuration keeps these JSON integers as JavaScript numbers and rejects values outside the safe integer range, rather than coercing them to `bigint`. Do not override DTO implementation types to influence client generation.

CI runs `api:check` on pull requests and pushes to `main`, then uses pinned oasdiff to reject incompatible contract changes against the PR base or previous pushed commit (manual runs compare the parent commit). The first contract establishes the baseline. Configure the GitHub `checks` job as a required branch-protection check to block merges; the upload job already depends on it. Preserve old API versions when making incompatible changes, since old browser tabs can still use older clients.

OpenAPI HTTP documentation is disabled in normal runtime to keep the public app minimal. The export test enables it only for generation. To inspect it during development, start Spring with `SPRINGDOC_API_DOCS_ENABLED=true` and open `/api/v1/v3/api-docs`.

## Deployment

Caddy routes `/api/*` to Spring Boot and everything else to SvelteKit.
The GitHub Actions workflow checks the app and uploads it to Tour de Cloud on pushes to `main` or manual runs. Configure the `TDC_TOKEN` and `POSTGRES_PASSWORD` repository secrets. After upload, select the version and click **Nasadit** in Tour de Cloud.

## Team storage

Lombok `@Data` generates the public no-argument constructor, accessors, equality, hashing, and string formatting. Callers populate `new TeamModel()` with setters and persist it using `repository.save(team)`. Its version is managed by the Spring Boot BOM.

`TeamModel` holds the entity mapping; `TeamRepository` provides database access through Spring Data JPA.

Spring Data JPA uses Hibernate to persist a `TeamModel` entity and its ordered member collection in PostgreSQL. Flyway creates `team` and `team_member`, then V2 renames the collection table to `team_members` and its columns to Hibernate defaults without changing stored values or order. The global `hibernate.mapping.default_list_semantics: LIST` setting preserves list order automatically; Hibernate validates the schema rather than modifying it. Flyway V3 inserts the team and members once if team ID 1 does not exist. Existing team data is preserved, and subsequent starts do not reinsert deleted or edited data. The HTTP endpoint always queries the database; it does not return the seed configuration. No public write endpoint is exposed.

The seed in `backend/src/main/resources/seed/V3__seed_team.sql` is **app = build $ replicate 2 Idiot**, with members **Lukáš Erl** and **Libor Martínek**. No team environment variables are required. Change seed data through a new migration after V3 has been applied; do not edit applied migrations. Existing data can be edited directly in PostgreSQL; reload the page to see the change.

Tour de Cloud's current deployment schema does not expose volume configuration. The bundled PostgreSQL container therefore does not guarantee data retention across redeployments; Flyway seeds the team again when initializing a fresh database. For durable cloud storage, use an externally managed PostgreSQL instance and configure `DATABASE_URL`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` on the server instead of the bundled database container.

## Stations

`StationModel` and `StationRepository` provide PostgreSQL persistence for stops. `StationRequest` and `StationResponse` are immutable API data objects, retaining the public `StopInput` and `Stop` schema names and snake_case JSON fields. `StationService` owns CRUD operations, DTO mapping, not-found checks, and transaction boundaries; `StationController` delegates to it and handles HTTP concerns. Flyway V4 creates `station` with a generated numeric ID, required nonblank name (up to 255 characters), optional image URL (up to 255 characters), decimal `x`/`y` coordinates, and boolean transfer/accessibility/facility flags defaulting to false. Hibernate derives snake_case column names automatically. Coordinates are not restricted to latitude/longitude ranges. V6 allows unknown coordinates (`null`) for stops created through the public API. Existing coordinates are preserved.

`/api/v1/stops` supports GET (list) and POST (create, 201); `/api/v1/stops/{id}` supports GET, PUT, and DELETE (204). Responses expose `id`, `name`, nullable `image_url`, `wheelchair_accessible`, `has_shelter`, and `has_ticket_machine`. Write requests require the name and all three booleans; omitted or null `image_url` clears the image on PUT. Names must be nonblank and at most 255 characters; images accept any string up to the database limit of 255 characters, or null; no URL or filename pattern is required. Invalid input returns 400, and missing IDs return 404. PUT preserves coordinates, transfer, bench, and display fields. New stops have unknown coordinates and false defaults for those internal flags. Lists are ordered by ID. The `/stops` frontend lists stops with images and links to `/stops/:id`, with loading, empty, retry, and missing-image states.

Seed assets and SQL seed scripts live in `backend/src/main/resources/seed`. Flyway scans both `db/migration` and `seed`. V3 retains the original team SQL seed. V5 streams the frozen `seed/v5/stops.csv` through PostgreSQL JDBC `COPY` into a temporary typed table, then inserts the supported station columns, preserving existing IDs and advancing the identity sequence. The CSV includes explicit image paths; Spring Boot serves PNGs from `seed/stopsImages` at `/api/v1/stops-images/**`. Line assignments remain in the CSV until a line model exists. CSV parsing is handled by PostgreSQL; Commons CSV is only a test dependency.

V5 runs once. Its checksum covers the CSV, staging/mapping SQL, and COPY command (normalizing CRLF for cross-platform checkouts). Treat `seed/v5` as immutable: subsequent data changes need a new migration and their own versioned resources. Restarts preserve edited and deleted rows. Flyway records the checksum when V5 is applied and validates it on subsequent starts. Do not delete migration history or automatically repair it during application startup.

## Frontend fixtures and assets

Faker.js (`@faker-js/faker`) powers the database-free development API in `frontend/dev/api.ts` and deterministic test fixtures in `frontend/tests/fixtures/stops.ts`. Pages use the same generated SDK and API URLs in both development and production. The mock API supports stop listing, detail, creation, editing, and deletion, with generated contract validation for writes.

Stop images use the API's `image_url` directly. The homepage previews the first stop with an image returned by the API. Its primary action remains available when that preview cannot load. Missing or failed images show a placeholder. Logo and font files live in `backend/src/main/resources/assets` and are served at `/api/v1/assets/**`. The frontend does not maintain local image copies or a derivative-generation script.
