# TradeWise AI — Backend

AI-powered Trading Journal and Portfolio Analyzer. This module is the Spring Boot
backend built with **Clean Architecture** (Controller → Service → Repository →
Store).

> **Phase 1 (current):** No database. All persistence is in-memory
> (`ConcurrentHashMap`) behind repository **interfaces**. Swapping in Spring Data
> JPA later requires no changes to the service/controller layers.

## Tech

- Java 21, Spring Boot 3.3.x
- Spring Web, Bean Validation, Spring Security (BCrypt only in Phase 1)
- Lombok, MapStruct
- springdoc-openapi (Swagger UI)

## Run

```powershell
cd backend
mvn spring-boot:run
```

- API base path: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Architecture

```
com.tradewise
├── common                 # cross-cutting building blocks
│   ├── config             # OpenAPI, Security (permit-all Phase 1 + BCrypt + CORS)
│   ├── domain             # BaseEntity (audit + soft delete)
│   ├── exception          # ApplicationException hierarchy + GlobalExceptionHandler
│   ├── pagination         # in-memory pagination util
│   ├── repository         # CrudRepository + InMemoryCrudRepository
│   ├── response           # ApiResponse<T>, ApiError, PagedResponse<T>
│   ├── security           # CurrentUserProvider (audit actor)
│   └── validation         # @FieldMatch, ValidationPatterns
├── auth                   # Feature 1 — Authentication & profile
├── portfolio              # Feature 2 — Portfolio dashboard (aggregator)
├── trade                  # Feature 3 — Trade history & analytics
├── watchlist              # Feature 4 — Watchlists, items, alerts
└── notes                  # Feature 5 — Notes / journal with versions
```

Each feature module follows the same layout: `domain` (+ `enums`), `dto`
(`request`/`response`), `repository` (interface + in-memory impl), `mapper`
(MapStruct), `service` (interface + impl) and `controller`.

## Conventions

- Every endpoint returns `ResponseEntity<ApiResponse<T>>`.
- Entities are **never** exposed; DTOs are used at all boundaries.
- Money is `BigDecimal` (scale 2).
- Soft delete everywhere via `BaseEntity` (`deleted`, `deletedBy`, `deletedDate`).
- Audit fields (`createdBy/Date`, `updatedBy/Date`) stamped by the service layer.
- Global exception handling yields a consistent error envelope.

### Temporary Phase-1 auth shim

Security (JWT) arrives in **Phase 3**. Until then:

- Authenticated endpoints identify the caller via the **`X-User-Id`** request
  header. This is replaced by the JWT principal (`@AuthenticationPrincipal`) in
  Phase 3 with no change to service signatures.
- Login/refresh issue **opaque** tokens via the `TokenService` abstraction; a
  signed-JWT implementation replaces `OpaqueTokenService` in Phase 3.
- `forgot-password` returns the OTP in `devOtp` for local testing only; real
  email delivery replaces this in a later phase.

## Key endpoints

| Area       | Method & Path |
|------------|---------------|
| Auth       | `POST /auth/register`, `POST /auth/login`, `POST /auth/refresh`, `POST /auth/logout`, `POST /auth/forgot-password`, `POST /auth/reset-password` |
| User       | `GET/PUT /users/me`, `PUT /users/me/password`, `DELETE /users/me` |
| Portfolio  | `POST /portfolio`, `GET /portfolio`, `PATCH /portfolio/cash`, `GET /portfolio/dashboard`, `/summary`, `/holdings`, `/allocation`, `/performance` |
| Trades     | `POST/PUT/GET/DELETE /trades`, `PATCH /trades/{id}/close`, `GET /trades` (search/filter/sort/paginate), `GET /trades/statistics` |
| Watchlists | `CRUD /watchlists`, `PATCH .../pin`, `.../favorite`, item CRUD, `PATCH .../items/reorder`, `GET .../items/{id}/alerts` |
| Notes      | `CRUD /notes`, `PATCH .../pin`, `.../favorite`, `.../archive`, `.../restore`, `GET .../versions` |

## Roadmap

- **Phase 2** — remaining APIs (CSV/PDF import/export, email verification).
- **Phase 3** — Security: JWT + refresh, Spring Security filter chain, rate
  limiting, CSRF/XSS hardening.
- **Phase 4** — React 19 + TypeScript frontend.
- **Phase 5** — JUnit / Mockito / integration tests.
- **Phase 6** — Docker deployment.
