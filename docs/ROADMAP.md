# Roadmap

Goal: get the app reliable and hosted so it can be used from a phone on a real trip, then build a Vue frontend. Each milestone is planned and landed as its own set of small PRs.

## M0 — Repo hygiene
- [ ] Untrack committed build output and IDE files: `bin/`, `lib/` (Jackson jars Maven already provides), `.vscode/` (gitignored but still tracked; stale launch configs)
- [ ] `pom.xml`: remove `exec-maven-plugin` (points at the commented-out `AppTest`), the redundant `jackson-databind` and `spring-boot-starter` entries, and the unused actuator
- [ ] Delete dead code: commented-out `SplitsService`, `SplitsRepository`, `AppTest`, `TestReadingJSONFile`
- [ ] Re-enable `contextLoads` so `./mvnw test` runs at least one test
- [ ] GitHub Actions workflow running `./mvnw test` on PRs

## M1 — Correctness + tests
- [ ] `Person.equals/hashCode`: `id == person.id` compares `Long` references (only works for ids −128..127) and treats all unsaved persons as equal
- [ ] One calculation implementation: move the `ExpenseService` calculation into a Spring-free calculator class; delete the legacy copy in `Expense` and its callers in `TripService`, `PersonService`, `DataLoader`
- [ ] Unit tests for the calculator using real numbers from the travel spreadsheet
- [ ] Derive participants from payers ∪ splits: today a payer who isn't listed in `participants` is silently dropped and balances no longer sum to zero
- [ ] Validation (Bean Validation + `@Valid`): payers sum to amount, splits sum to amount, rate > 0, at least one payer and split
- [ ] Rounding policy: even splits lose the remainder (100 / 3 → 99.999 total) and converted trip balances drift (seed data shows +4.707 / −4.708); decide scale and who absorbs remainders
- [ ] `@RestControllerAdvice` for consistent 400/404 responses
  - `GET /api/trips/{missing}` → 500 (NPE in `convertToDTO`)
  - `GET /api/persons/{missing}` → 200 with empty body
  - `DELETE /api/trips/{missing}` → 204; the 404 branch is dead code because `deleteById` no longer throws in Spring Data 3
- [ ] DTO-only API: `/api/expenses` POST/PUT/even-split and `/api/persons` still take/return entities; `POST /api/trips` returns the `Trip` entity with 200 instead of a `TripDTO` with 201
- [ ] Fix `PUT /api/expenses/{id}`: moved payers/splits still reference the unsaved request object, and participants/trip/currency aren't updated
- [ ] `GET /api/persons/{id}` returns empty debts (only `getAllPersons` calculates them)
- [ ] MockMvc tests for every endpoint

## M2 — Trip features
- [ ] Participants scoped to a trip (today `Person` is global, and DTO maps keyed by name collide if two people share a name)
- [ ] Trip has `homeCurrency`, `localCurrency` and a default exchange rate; expenses default to it and can override; `currencyCode` actually populated
- [ ] Dates as `LocalDate` instead of `String`
- [ ] Record paybacks/settlements (unused `isPayBack` flag today)
- [ ] Trip-wide settle-up endpoint returning the minimum set of transfers. Today `/api/persons` shows raw pairwise debts (e.g. Van owes Henry 700 while Henry owes Van 1000) instead of netting them
- [ ] Full update/delete for trips, expenses and people (deleting a person referenced by an expense currently fails with an FK error)

## M3 — Persistence + deployment
- [ ] Merge the `docker` branch: Postgres via Docker Compose with a pinned image version
- [ ] `dev` / `prod` profiles; datasource from environment variables (the branch hardcodes `db:5432`, which breaks running outside Compose)
- [ ] Flyway migrations replace `ddl-auto=create-drop`; `DataLoader` only in `dev`
- [ ] Single-user authentication (Spring Security HTTP Basic) before anything is public
- [ ] Postman collection committed alongside `request.http`
- [ ] Deploy API + database to a host (choose when we get here)

## M4 — Vue frontend
- [ ] `frontend/` with Vite + Vue 3
- [ ] Mobile-first screens: trip list, add expense, balances / settle up
- [ ] Dev proxy / CORS config; deploy alongside the API

## Later
- Accounts so friends can add their own expenses
- CSV export
- Testcontainers for Postgres-backed integration tests
- Auto-fill exchange rates by date via `util/ExchangeRateFetcher` (Frankfurter API)
