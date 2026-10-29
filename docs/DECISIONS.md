# Design decisions

Short log of choices that aren't obvious from the code. Newest first. Format: decision, why, and what we gave up.

## 2026-10-29 — Restart plan

**Hosted, phone-first.** The app is only useful if I can log an expense at the restaurant table, so it needs to run somewhere reachable from a phone. That puts Postgres, migrations and deployment ahead of new features.

**Single user, minimal auth.** I'll enter everything myself on trips, so no accounts yet. Because it will be on the public internet, it still gets a single-user login (HTTP Basic) before deploy. Friend accounts are a later milestone.

**Home currency + one local currency per trip, manual rate.** This matches how I actually tracked trips in a spreadsheet: one exchange rate for the trip, adjusted per expense when needed. Multi-currency trips and automatic rates (`ExchangeRateFetcher` already exists) can come later.

**DTOs at the API boundary.** Entities have bidirectional JPA relationships and transient derived maps; exposing them directly couples the JSON shape to the database model and causes serialization issues. Controllers take `*CreateDTO` and return `*DTO`.

**Derived balances are computed, not stored.** Balances and debts are recomputed from payers and splits on read, so there is a single source of truth and editing an expense can't leave stale totals. The cost is recomputation per request, which is fine at this scale.

**Vue.js frontend, Java backend.** The backend stays Java/Spring Boot; the frontend will be a separate Vue 3 app in `frontend/`.

**AI-assisted development.** From this point the project is built with AI assistance (Claude Code): I plan and make design decisions, AI implements in small PRs, and I review every change. `CLAUDE.md` holds the conventions both of us follow. The original domain model and calculation logic were written by hand before this.
