# [E13] Issue 126: ROLE_DESIGNER — Edit, Propose and Approve Distributions

Status: Draft
Issue: #126
Epic: #118
Related: E12 #116 (`ROLE_MEASURER`, `assignedMeasurer`), #121, #123, #124, #125, #127
Owner: TBD

## Problem

Studio has only `ROLE_ADMIN` and `ROLE_USER` (+ `ROLE_MEASURER` from E12).
Editing and approving distributions — which trigger fabrication artifacts —
should not require full admin rights, and must not be open to every user.

## Decision

New authority **`ROLE_DESIGNER`** ("Diseñador"), plus
`DesignSession.assignedDesigner` (User, nullable). Roles are combinable (a
designer who also measures has both roles).

## Permission matrix

| Action | ADMIN | DESIGNER | MEASURER | USER |
| --- | --- | --- | --- | --- |
| View session (Studio) | all | assigned + unassigned | — | existing rules |
| Mobile: list/sync assigned sessions, measurement, v0 | ✔ | only if also MEASURER | assigned | — |
| View measurement and distributions | ✔ | ✔ (visible sessions) | assigned (mobile) | — |
| Create/edit `DRAFT` versions | ✔ | ✔ | — | — |
| Request AI proposals | ✔ | ✔ | — | — |
| Acknowledge acknowledgeable errors | ✔ | ✔ | — | — |
| Approve version | ✔ | ✔ | — | — |
| Generate CSV / Fusion / prototype | ✔ | ✔ | — | — |
| Assign measurer / designer | ✔ | assign measurer only | — | — |
| Configure rule parameters (#127) | ✔ | — | — | — |
| Manage users/roles | ✔ | — | — | — |

## Data Model Impact

- Liquibase: insert `ROLE_DESIGNER` into `jhi_authority` (new changeset).
- `AuthoritiesConstants.DESIGNER`.
- JDL: `DesignSession` many-to-one `User` as `assignedDesigner` (nullable).

## API Impact

- #121/#123/#124/#125 endpoints: `hasAnyAuthority(ROLE_ADMIN, ROLE_DESIGNER)`
  plus session visibility check (service layer).
- `PUT /api/design-sessions/{id}/assigned-designer` (ADMIN).
- E12 #116 `assigned-measurer` endpoint: also allowed for DESIGNER.

## Frontend

- Session detail: "Diseñador asignado" select (ADMIN); menu items for the
  distribution editor shown only to ADMIN/DESIGNER.

## Acceptance Criteria

- [ ] `ROLE_DESIGNER` exists and is grantable in user admin.
- [ ] Security ITs cover every row of the permission matrix.
- [ ] A designer cannot change rule parameters or manage users.
- [ ] A measurer cannot approve or generate fabrication artifacts.

## Open Questions

- [ ] Should designers see **all** sessions (small team) instead of assigned + unassigned?
