# [E12] Issue 116: ROLE_MEASURER and Session Assignment

Status: Draft
Issue: #116
Epic: #104
Related: #115 (mobile auth), #112 (sync API), outis10/KFS-APP#10 (session list), outis10/KFS-APP#6
Owner: TBD

## Problem

Studio has only `ROLE_ADMIN` and `ROLE_USER`, and `DesignSession` has no owner
or assignee. The mobile app needs to know **who** may measure and **which**
sessions each person should see, without exposing chat, quotes or other
clients' data.

## Decision (recommended)

- New authority **`ROLE_MEASURER`** ("Medidor en obra"): the person who visits
  the site. Least privilege: sees only sessions assigned to them, can create,
  sync and confirm measurements for those sessions, and download the backup
  sheet. No access to chat, quotes, catalog admin or other sessions.
- `ROLE_ADMIN` keeps full access and can also measure.
- A user can hold `ROLE_MEASURER` together with other roles (e.g. a designer
  who also measures).
- Assignment on the session: `DesignSession.assignedMeasurer` (User).

Why not reuse `ROLE_USER`: it is the generic authenticated role (clients and
staff); granting measurement rights to it would expose data to everyone.

## Non-Goals

- A full designer/sales role model (possible later: `ROLE_DESIGNER`).
- Visit scheduling/calendar.

## Data Model Impact

- Liquibase: new changeset inserting `ROLE_MEASURER` into `jhi_authority`.
- `AuthoritiesConstants.MEASURER = "ROLE_MEASURER"`.
- JDL: `DesignSession` many-to-one `User` as `assignedMeasurer` (nullable),
  regenerated; new changelog for the FK column.

## API Contract

`PUT /api/design-sessions/{id}/assigned-measurer` (ROLE_ADMIN)
- Body: `{ "userLogin": "ana" }` or `{ "userLogin": null }` to unassign.
- `400` if user lacks `ROLE_MEASURER`; `404` session/user.

`GET /api/mobile/sessions` (ROLE_MEASURER or ROLE_ADMIN)
- Measurer: sessions assigned to them with status before `MEASURED` or with a
  draft measurement. Admin: all with a measurer filter param.
- Minimal DTO: `id`, `sessionCode`, `projectType`, `status`, client display
  name, optional site address (open question), `latestMeasurement` summary.
- No email, phone, chat or quote data.

Authorization for #112 / #106 endpoints: `ROLE_ADMIN` **or**
(`ROLE_MEASURER` **and** `session.assignedMeasurer = currentUser`).

## Frontend (Studio web)

- Session detail: "Medidor asignado" select (users with `ROLE_MEASURER`),
  in `modules/` (not `entities/`).
- Admin user management: `ROLE_MEASURER` selectable (JHipster user admin lists authorities from DB).

## Acceptance Criteria

- [ ] `ROLE_MEASURER` exists after migration and can be granted in user admin.
- [ ] A measurer only sees and syncs assigned sessions (`403` otherwise).
- [ ] Mobile session DTO contains no email, phone, chat or quote data.
- [ ] Admin can assign/unassign from the session detail.

## Test Plan

- Backend: security ITs per role (admin, measurer assigned, measurer not assigned, user).
- Frontend: assign select test.

## Open Questions

- [ ] Cache the site address on the phone for navigation (privacy trade-off)?
- [ ] More than one measurer per session?
