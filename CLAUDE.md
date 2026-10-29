# CLAUDE.md

Engineering notes for working in this repo (humans and AI assistants alike).

## Overview
Trip expense splitter. A `Trip` has participants and `Expense`s; each expense records who paid (`Payer`) and who is responsible for how much (`Splits`). The API computes per-expense balances, who owes whom, and net balances per trip, in both the local and home currency.

Stack: Java 21, Spring Boot 3.5, Spring Data JPA, H2 (dev). Postgres + Docker live on the unmerged `docker` branch (see `docs/ROADMAP.md`, M3).

## Commands
```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run — API on http://localhost:8080
./mvnw test                   # run tests
./mvnw clean package          # build jar into target/
```
- H2 console (dev): http://localhost:8080/h2-console. JDBC URL is printed in the startup log.
- Manual endpoint testing: `request.http` (VS Code REST Client / IntelliJ HTTP client).
- The `dev` profile is active by default and `DataLoader` seeds a sample "Japan" trip (Henry, Van, Khoa; ids 1–3).

## Layout
```
src/main/java/com/altair/expensetracker/
  controller/   REST endpoints (/api/trips, /api/expenses, /api/persons)
  service/      business logic, DTO <-> entity conversion, calculations
  repository/   Spring Data JPA interfaces
  entity/       JPA entities: Trip, Expense, Payer, Splits, Person
  dto/          request (*CreateDTO) and response (*DTO) shapes
  config/       DataLoader (dev seed data)
  util/         ExchangeRateFetcher (Frankfurter API, not wired in yet)
```

## Architecture rules
- Layering is controller → service → repository. Controllers stay thin: map HTTP to a service call and back.
- Controllers accept and return DTOs only, never entities. Request shapes are `*CreateDTO`; response shapes are `*DTO`. Some older `/api/expenses` and `/api/persons` endpoints still take entities and are scheduled to change (M1).
- Calculation logic has exactly one home, in the service layer (moving to a Spring-free calculator in M1). Entities hold state; they don't calculate.
- Derived data (`payerMap`, `splitsMap`, `expenseBalance*`, `debts*`, `simpleDebt*`) is `@Transient` and recomputed on read. Don't persist it, and don't compute it twice in the same request: the `debts` maps use `merge` and will double count.
- Constructor injection for all Spring beans (no `@Autowired` fields).

## Domain conventions
- Money is `BigDecimal`, never `double`/`float`. Construct from strings (`new BigDecimal("12.50")`), compare with `compareTo`, not `equals`.
- Sign convention: **balance = split − paid**. Positive means the person owes money; negative means they are owed.
- Exchange rate = local currency units per 1 unit of home currency (e.g. 148.7 JPY per USD). Converted amount = local amount ÷ rate.
- Divisions currently use scale 3, `RoundingMode.HALF_UP`. TODO (M1): settle a rounding policy and where even-split remainders go so totals always reconcile.
- Payers and splits for an expense must each sum to the expense amount (not enforced yet, M1).

## Code style
- Match the surrounding code: `getID()` naming, one-line getters/setters, short `//` comments explaining intent.
- No new commented-out code blocks; delete dead code instead (git has history).
- Keep JSON field names stable; if a DTO field is renamed internally, keep the `@JsonProperty` name.

## Testing
- JUnit 5 + AssertJ + Mockito via `spring-boot-starter-test`.
- Any change to calculation logic needs unit tests with concrete numbers (expected balances and debts written out by hand).
- Any endpoint change needs a MockMvc test (`@WebMvcTest` or `@SpringBootTest` + `@AutoConfigureMockMvc`) and an updated `request.http` entry (and the Postman collection once it exists).
- Tests must not depend on network access (`ExchangeRateFetcher` gets mocked).

## Workflow
- One concern per branch/PR, small reviewable diffs, branch off `main`.
- Don't commit, push, or open PRs unless asked.
- Keep `docs/ROADMAP.md` checkboxes current as work lands; record non-obvious design choices in `docs/DECISIONS.md`.

## Known issues (until fixed)
- `Person.equals` compares `Long` ids with `==` (reference equality; breaks for ids > 127) and treats all unsaved persons as equal. Affects every `Map<Person, …>`.
- `spring.jpa.hibernate.ddl-auto=create-drop` + in-memory H2: all data is wiped on restart and reseeded by `DataLoader`.
- Two copies of the calculation exist: `ExpenseService` (current) and the legacy methods in `Expense` still called by `TripService`, `PersonService`, `DataLoader`.
- Full list with planned fixes: `docs/ROADMAP.md`.
