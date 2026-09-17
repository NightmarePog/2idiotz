# Think diffrent Academy

Minimal SvelteKit frontend and Spring Boot backend.

The frontend uses Tailwind CSS and shadcn-svelte. Page layout uses utility classes; `src/app.css` contains the shared theme. Add components as needed with `pnpm exec shadcn-svelte add <component>` from `frontend`.

- `/` displays **Think diffrent Academy**, calls the health endpoint, and shows **Status: OK**.
- `GET /api/v1/health` returns `200 {"status":"ok"}`.

## Run with Docker

```sh
docker compose up --build -d
```

Open http://localhost:8080. Stop with `docker compose down`. No database or environment secrets are needed locally.

## Development

Use Java 21, Node 22.22.2 or newer, pnpm 10.34.5, and Python 3.

Use Python's standard library for automation involving files or JSON, and shell for short command sequences. Keep tool configuration and application tests in their native formats; do not add handwritten JavaScript automation scripts.

```sh
cd backend
./gradlew bootRun
```

In another terminal:

```sh
cd frontend
pnpm install --frozen-lockfile
pnpm dev
```

Vite forwards `/api` to the backend on port 8080.

The backend sets `/api/v1` globally with `server.servlet.context-path` in `backend/src/main/resources/application.yaml`. Controllers declare only their resource path, such as `@GetMapping("/health")`. Both Caddy and Vite preserve the full request path when proxying to the backend.

## Checks

In `backend`, run `./gradlew check` (formatting and HTTP contract test).
In `frontend`, run `pnpm check`, `pnpm lint`, and `pnpm test` (desktop/mobile browser tests).
Install the browser once with `pnpm exec playwright install chromium`.
Run `python3 scripts/smoke.py` against a running Docker Compose stack to check the homepage, assets, and API routing.

## Typed API workflow

Java controllers and DTOs are the source of truth. Springdoc exports OpenAPI; Hey API generates the TypeScript SDK and Zod validators. The homepage calls `getHealth()` from `src/lib/api/generated`, with runtime validation enabled. The contract explicitly requires `status: "ok"`.

After changing a controller or DTO, run from `frontend`:

```sh
pnpm api:generate
```

This exports the contract through the existing Gradle task and generates the client. The export task uses a temporary Spring test server on a random port; no separately running backend or database is needed. Commit `frontend/openapi.json` and the entire `frontend/src/lib/api/generated` directory with your Java changes; never edit generated files by hand.

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

`test:api` validates the exported live backend response through the generated SDK and tests invalid responses. Run `api:generate` or `api:check` first to produce that response fixture. CI runs all three commands. Generator and validator versions are pinned. Generated code is excluded from formatting/lint rules, but remains type-checked. Application code uses the generated SDK; lint rejects direct global `fetch` calls in `src`.

For GET endpoints, use `@ApiGet(path = "/resource", operationId = "getResource")`. It combines Spring GET routing, JSON output, and a stable OpenAPI operation ID. Other HTTP methods use their standard Spring mapping and `@Operation` annotations.

Use concrete DTOs and standard Jakarta validation constraints (`@NotNull`, `@NotBlank`, `@Size`, etc.) for required fields and restrictions. Use Java enums for finite allowed values; `@JsonProperty` can give enum constants their JSON spelling. Springdoc derives the schema from these types and constraints, so routine fields do not need `@Schema`. Health uses `@NotNull Status status` with `Status.OK` serialized as `"ok"`. Constraints are enforced when validation is invoked (for example, `@Valid` on a request body); they do not automatically reject null values passed to record constructors. Reserve `@Schema` for documentation or details that cannot be inferred. Add contract coverage for new responses; current live contract coverage is the health endpoint.

CI runs `api:check` on pull requests and pushes to `main`, then uses pinned oasdiff to reject incompatible contract changes against the PR base or previous pushed commit (manual runs compare the parent commit). The first contract establishes the baseline. Configure the GitHub `checks` job as a required branch-protection check to block merges; the upload job already depends on it. Preserve old API versions when making incompatible changes, since old browser tabs can still use older clients.

OpenAPI HTTP documentation is disabled in normal runtime to keep the public app minimal. The export test enables it only for generation. To inspect it during development, start Spring with `SPRINGDOC_API_DOCS_ENABLED=true` and open `/api/v1/v3/api-docs`.

## Deployment

Caddy routes `/api/*` to Spring Boot and everything else to SvelteKit.
The GitHub Actions workflow checks the app and uploads it to Tour de Cloud on pushes to `main` or manual runs. Configure the `TDC_TOKEN` repository secret. After upload, select the version and click **Nasadit** in Tour de Cloud.
