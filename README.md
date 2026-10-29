# ExpenseTracker

A REST API for splitting shared expenses on group trips, built with Java and Spring Boot.

On trips with friends, someone pays for dinner, someone else covers the train, and a third person only had a coffee. I used to track this in a spreadsheet; this project turns that spreadsheet into an application. For every expense it records who paid, who is responsible for how much, and works out each person's balance, who owes whom, and the net totals for the whole trip, in both the local currency and your home currency.

## Features

**Working today**
- Trips with participants, and expenses attached to a trip
- Multiple payers per expense and custom (uneven) or even splits
- Per-expense balance for each person (positive = owes, negative = is owed)
- Per-person debts (who owes whom, and how much)
- Trip-wide net balance per person
- Every amount also converted to the home currency using the expense's exchange rate

**Planned** (see [docs/ROADMAP.md](docs/ROADMAP.md))
- Trip-wide "settle up": the minimum set of payments to square everyone up
- Recording paybacks
- Input validation and consistent error responses
- PostgreSQL + Docker, persistent data, and a hosted deployment
- A mobile-friendly Vue.js frontend for logging expenses on the go

## Tech stack
- Java 21, Spring Boot 3.5 (Web, Data JPA)
- H2 in-memory database for development (PostgreSQL in progress)
- Maven (wrapper included)
- JUnit 5 / Spring Boot Test

## Running locally
Requires JDK 21.

```bash
./mvnw spring-boot:run
```
On Windows use `mvnw.cmd spring-boot:run`. The API starts on `http://localhost:8080`.

The `dev` profile seeds a sample trip, "Japan", with three people (Henry, Van, Khoa: ids 1–3) and three expenses. Data lives in memory and resets on every restart. The H2 console is at `http://localhost:8080/h2-console`; the JDBC URL is printed in the startup log.

## API

| Method | Path | Description |
|---|---|---|
| GET | `/api/trips` | All trips with net balances |
| GET | `/api/trips/{id}` | One trip with net balances |
| POST | `/api/trips` | Create a trip from existing people |
| DELETE | `/api/trips/{id}` | Delete a trip and its expenses |
| GET | `/api/trips/{tripId}/expenses` | Expenses in a trip |
| GET | `/api/trips/{tripId}/expenses/{expenseId}` | One expense in a trip |
| POST | `/api/trips/{tripId}/expenses` | Add an expense to a trip |
| GET | `/api/expenses` | All expenses |
| GET | `/api/expenses/{id}` | One expense |
| DELETE | `/api/expenses/{id}` | Delete an expense |
| GET | `/api/persons` | All people with their debts |
| GET | `/api/persons/{id}` | One person |
| POST | `/api/persons` | Create a person: `{"name": "Alice"}` |

`/api/expenses` also has older `POST`, `POST /even-split` and `PUT /{id}` endpoints that are being reworked; use the trip-scoped endpoint to create expenses.

### Example: add an expense to a trip

`POST /api/trips/1/expenses`: ¥3000 lunch; Henry paid ¥2000, Van paid ¥1000, split evenly three ways, at 148 JPY per USD.

```json
{
  "title": "Lunch",
  "amount": 3000,
  "date": "2025-10-06",
  "exchangeRate": 148,
  "participants": [{ "id": 1 }, { "id": 2 }, { "id": 3 }],
  "payers": [
    { "person": { "id": 1 }, "amount": 2000 },
    { "person": { "id": 2 }, "amount": 1000 }
  ],
  "splits": [
    { "person": { "id": 1 }, "amount": 1000 },
    { "person": { "id": 2 }, "amount": 1000 },
    { "person": { "id": 3 }, "amount": 1000 }
  ]
}
```

Response `201 Created`:

```json
{
  "expenseID": 4,
  "tripID": 1,
  "title": "Lunch",
  "amount": 3000,
  "date": "2025-10-06",
  "exchangeRate": 148,
  "participants": ["Henry", "Van", "Khoa"],
  "payers": { "Henry": 2000, "Van": 1000 },
  "splits": { "Henry": 1000, "Van": 1000, "Khoa": 1000 },
  "balance": { "Henry": -1000, "Van": 0, "Khoa": 1000 },
  "convertedBalance": { "Henry": -6.757, "Van": 0.000, "Khoa": 6.757 }
}
```

Khoa owes ¥1000 (≈ $6.76), Henry is owed ¥1000, Van is even.

## How balances are calculated
For each person on an expense:

```
balance = amount they're responsible for (split) − amount they paid
```

A positive balance means they owe money; negative means they're owed. Debts are assigned by matching each person who owes against people who are owed until their balance is cleared. A trip's net balance is the sum of each person's balances across its expenses. Converted values divide by the exchange rate (local units per 1 home-currency unit).

## Testing
- `./mvnw test` runs the test suite (being rebuilt; see the roadmap).
- [`request.http`](request.http) has ready-made requests for every endpoint and runs in VS Code (REST Client extension) or IntelliJ's HTTP client.

## Project history
This started in my first year of CS as a plain-Java object-oriented design exercise: `Expense`, `Person` and `Trip` classes driven from a `main` method. It then grew into a Spring Boot application with JPA persistence, a layered controller/service/repository design, and DTOs at the API boundary. The current focus is making it reliable enough to use on a real trip, then adding a frontend. Design decisions are logged in [docs/DECISIONS.md](docs/DECISIONS.md).
