# [E12] Issue 116: ROLE_MEASURER and Session Assignment

Status: Reviewed
Issue: #116
Epic: #104
Related: #115 (mobile auth), #112 (sync API), #106 (backup sheet), #114 (app config),
E13 #126 (`ROLE_DESIGNER`), #142 (CRUD exposure), outis10/KFS-APP#10 (session list), outis10/KFS-APP#6
Owner: TBD

## Problem

Studio has only `ROLE_ADMIN` and `ROLE_USER`, and `DesignSession` has no owner
or assignee. The mobile app needs to know **who** may measure and **which**
sessions each person should see, without exposing chat, quotes or other
clients' data.

## Current state (verified 2026-10-09)

- `SecurityConfiguration`: everything under `/api/**` only requires
  authentication; no `@PreAuthorize` on the generated CRUD resources, and the
  generated list endpoints (`/api/design-sessions`, `/api/chat-messages`,
  `/api/quotes`, …) return **all** rows.
- Any Google sign-in creates an activated `ROLE_USER` account
  (`GoogleAuthResource`), and `/api/register` is open.
- Consequence: today any authenticated account can read every session,
  including `clientEmail` / `clientPhone`, chats and quotes. Granting a
  measurer `ROLE_USER` would give them the same access.
- `DesignSession` already has `projectType` and `clientName`; there is no
  site address field.

## Decision

- New authority **`ROLE_MEASURER`** ("Medidor en obra"): the person who visits
  the site. Least privilege: sees only sessions assigned to them, can create,
  sync and confirm measurements for those sessions, and download the backup
  sheet. No access to chat, quotes, catalog admin or other sessions.
- **Measurer-only accounts get `ROLE_MEASURER` without `ROLE_USER`.** The
  generic rule becomes `/api/**` → `hasAnyAuthority(ROLE_ADMIN, ROLE_USER)`,
  placed after the measurer matchers below, so an account with only
  `ROLE_MEASURER` cannot reach the generated CRUD endpoints. Measurer-only
  accounts cannot use the Studio web app (web routes require `ROLE_USER`);
  they work from the mobile app.
- Roles are combinable: a designer who also measures holds `ROLE_DESIGNER` (E13
  #126) + `ROLE_MEASURER` (+ `ROLE_USER` for the web).
- `ROLE_ADMIN` keeps full access and can also measure.
- **One measurer per session** (`DesignSession.assignedMeasurer`, nullable).
  Matches the sync conflict strategy in the plan (one device editing a session).
- Who assigns: `ROLE_ADMIN`; also `ROLE_DESIGNER` once E13 #126 exists.
- **No site address in v1.** There is no address field and caching client
  addresses on phones is a privacy cost; the measurer gets the address through
  the usual channel. Revisit if a field is added to `DesignSession`.

The wider exposure of the generated CRUD endpoints to every `ROLE_USER` (clients
included) is **out of scope** here and tracked in #142 (see *Follow-up*).

## Non-Goals

- `ROLE_DESIGNER` and its permission matrix (E13 #126).
- Restricting what `ROLE_USER` can read in the generated CRUD endpoints (#142).
- Visit scheduling/calendar.

## Data Model Impact

- Liquibase: new changeset inserting `ROLE_MEASURER` into `jhi_authority`
  (new id; `authority.csv` seed is not edited).
- `AuthoritiesConstants.MEASURER = "ROLE_MEASURER"`; frontend `Authority.MEASURER`.
- JDL: `relationship ManyToOne { DesignSession{assignedMeasurer(login)} to User with builtInEntity }`
  (nullable), regenerated; new changelog for the FK column + index.

## Security configuration

Order matters (first match wins):

| Matcher | Rule |
| --- | --- |
| `GET /api/mobile/app-config`, `GET /api/mobile/releases/**` | as defined in #114 |
| `/api/mobile/auth/**` | as defined in #115 |
| `/api/mobile/**` | `hasAnyAuthority(ROLE_ADMIN, ROLE_MEASURER)` |
| `/api/design-sessions/*/site-measurements/**` (#112), `GET /api/design-sessions/*/backup-sheet.pdf` (#106) | `hasAnyAuthority(ROLE_ADMIN, ROLE_MEASURER)` + service-level assignment check |
| `GET /api/croquis/catalog` (#105) | `hasAnyAuthority(ROLE_ADMIN, ROLE_USER, ROLE_MEASURER)` |
| `/api/account`, `/api/account/change-password` | `authenticated()` (unchanged) |
| `/api/**` | `hasAnyAuthority(ROLE_ADMIN, ROLE_USER)` (was `authenticated()`) |

Assignment check (service layer, `SessionAccessService.requireMeasurementAccess(sessionId)`):
`ROLE_ADMIN` **or** (`ROLE_MEASURER` **and** `session.assignedMeasurer = currentUser`);
otherwise `403`. Used by #112, #106 and `GET /api/mobile/sessions/{id}`.

## API Contract

### Assign / unassign

`PUT /api/design-sessions/{id}/assigned-measurer`

- Auth: `ROLE_ADMIN` (and `ROLE_DESIGNER` after #126).
- Body: `{ "userLogin": "ana" }`, or `{ "userLogin": null }` to unassign.
- `200`: `{ "sessionId": 12, "assignedMeasurer": { "login": "ana", "firstName": "Ana", "lastName": "López" } }`
  (`assignedMeasurer: null` after unassign). Idempotent.
- `400 USER_NOT_MEASURER` if the user lacks `ROLE_MEASURER`; `400 USER_NOT_ACTIVATED`;
  `404` session or user; `403` other roles.
- Reassigning while the previous measurer has unsynced work is allowed: from
  then on their syncs get `403`; the web shows a warning when the session has a
  `DRAFT` measurement (after #107).

### Assignable measurers (for the web select)

`GET /api/measurers`

- Auth: `ROLE_ADMIN` (and `ROLE_DESIGNER` after #126). `/api/admin/users` is admin-only and paged, so it is not reused.
- `200`: `[ { "login": "ana", "firstName": "Ana", "lastName": "López" } ]` —
  activated users with `ROLE_MEASURER`, sorted by name. No email/phone.

### Mobile session list

`GET /api/mobile/sessions`

- Auth: `ROLE_MEASURER` or `ROLE_ADMIN`.
- Measurer: sessions where `assignedMeasurer = currentUser` and status not in
  (`COMPLETED`, `ARCHIVED`). Re-measuring a session that already has a
  confirmed measurement stays possible (#107 decides new measurement vs new revision).
- Admin: all non-closed sessions; optional `?measurer={login}`; paged (`page`, `size`, default 50).
- `200`:

```json
[
  {
    "id": 12,
    "sessionCode": "KD-2026-022",
    "projectType": "KITCHEN",
    "status": "SPECS_READY",
    "clientName": "Familia Pérez",
    "assignedAt": "2026-10-09T15:00:00Z",
    "latestMeasurement": { "measurementUuid": "uuid", "revision": 4, "status": "DRAFT", "capturedAt": "2026-10-08T16:00:00Z" }
  }
]
```

- `latestMeasurement` is `null` until #107 exists / when there is none.
- `assignedAt` requires storing the assignment time: `DesignSession.measurerAssignedAt` (Instant, nullable), set by the assign endpoint.
- Never includes `clientEmail`, `clientPhone`, notes, chat, images, quotes or artifacts.

`GET /api/mobile/sessions/{id}` → same DTO; `403` if not assigned (measurer), `404` unknown.

## Backend Behavior

- Resources in `web/rest/custom/`: `MeasurerAssignmentResource`, `MobileSessionResource`.
- Services: `MeasurerAssignmentService`, `MobileSessionService`, `SessionAccessService` (+ Impl).
- Repository: `findByAssignedMeasurerLoginAndStatusNotIn(...)` with `@EntityGraph`
  as needed; dedicated `MobileSessionDTO` + mapper (not `DesignSessionDTO`).
- Assignment in one transaction; audit log line with session, old/new login, actor (no client PII).

## Frontend (Studio web)

- Session detail (`modules/`): "Medidor asignado" select fed by `GET /api/measurers`,
  visible to ADMIN (and DESIGNER after #126).
  - Loading: disabled select with spinner. Empty: "No hay usuarios con rol de medidor" + link to user admin (ADMIN).
  - Error: inline alert, previous value kept. Success: toast "Medidor asignado". Unassign: "Sin asignar" option.
  - Mobile width: select full width below the session header.
- User admin: `ROLE_MEASURER` selectable automatically (authorities come from the DB);
  add the es/en label "Medidor en obra" / "Site measurer".

## Acceptance Criteria

- [ ] `ROLE_MEASURER` exists after migration and can be granted in user admin.
- [ ] A measurer-only account gets `403` on generated CRUD endpoints (e.g. `GET /api/design-sessions`, `/api/chat-messages`, `/api/quotes`).
- [ ] A measurer sees only assigned, non-closed sessions in `GET /api/mobile/sessions`; `403` on others' `GET /api/mobile/sessions/{id}`.
- [ ] `MobileSessionDTO` contains no email, phone, notes, chat, image, quote or artifact data (JSON field whitelist test).
- [ ] Admin can assign/unassign; assigning a non-measurer returns `400 USER_NOT_MEASURER`.
- [ ] `ROLE_USER` and `ROLE_ADMIN` behavior on existing endpoints is unchanged (existing ITs pass).

## Test Plan

- Backend ITs per role — admin, measurer assigned, measurer not assigned,
  measurer-only on CRUD, user, anonymous — for assign, measurers list, mobile list/detail.
- Security config IT: matcher order (measurer-only → `403` on `/api/design-sessions`, `200` on `/api/mobile/sessions`, `200` on `/api/croquis/catalog`).
- DTO whitelist test.
- Frontend: select states (loading, empty, error, success) with Jest/RTL.

## Follow-up (#142)

Generated CRUD endpoints are readable by any `ROLE_USER`, and Google sign-in /
registration create `ROLE_USER` accounts automatically → any person with a
Google account can list every client's sessions, contact data, chats and
quotes. Tracked in #142 (priority high, required before any public
deployment / MVP): restrict generated CRUD to `ROLE_ADMIN` (and
staff roles), and review automatic `ROLE_USER` on Google sign-in/registration.

## Open Questions

Resolved at review (2026-10-09):

- [x] Site address on the phone — not in v1 (no field; privacy).
- [x] More than one measurer per session — no, one.
- [x] Session list filter — assigned and not `COMPLETED`/`ARCHIVED`; independent of where `MEASURED` sits (#107).
- [x] Who assigns — ADMIN, plus DESIGNER after #126.
