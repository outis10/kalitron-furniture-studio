# [E13] Issue 124: Approve Distribution Version; CSV/Fusion Only from Approved

Status: Draft
Issue: #124
Epic: #118
Related: E6 `CabinetPlanService`, E11 #99–#103 (Fusion script), gateway `/generate-csv`, E8 prototype
Owner: TBD

## Goal

Approving a version materializes the cabinet plan and is the only way to
produce the Fusion CSV and downstream fabrication artifacts.

## API Contract

`POST /api/design-sessions/{sessionId}/distributions/{version}/approve`

- Auth: `ROLE_ADMIN` or `ROLE_DESIGNER` (#126).
- Preconditions: version `DRAFT` or `PROPOSED`; session measurement `CONFIRMED`
  (E12); zero unacknowledged `ERROR`s after server re-validation.
- Response `200`: `{ versionNumber, status: "APPROVED", cabinetCount, sessionStatus: "LAYOUT_CONFIRMED" }`.
- Errors: `409` wrong status; `422` with `validationIssues[]` or `MEASUREMENT_NOT_CONFIRMED`.

## Behavior (one transaction)

1. Re-validate (#122).
2. Previous `APPROVED` → `SUPERSEDED`.
3. Replace the session's `Cabinet` rows: one per `MODULE` and `FILLER` item,
   `cabinetCode` `{wall}-{seq}`, `wallCode`, `positionX` = derived X,
   `positionZ` = 0 for base/tall and `mountHeightMm` for uppers, template
   dimensions, `distributionItemUuid`. Freestanding runs (#128): room
   `positionX/positionY` + `rotationDeg`, `runCode`. `APPLIANCE_SLOT` and `GAP` produce no cabinets.
4. `SessionStatus` → `LAYOUT_CONFIRMED`.
5. Mark artifacts from older versions (`DesignArtifact.distributionVersion` <
   approved) as stale in responses.

## Fusion / CSV gate

- CSV generation (Studio → gateway `/generate-csv`), Fusion script (E11) and
  E8 prototype generation require an `APPROVED` distribution; otherwise `409
  DISTRIBUTION_NOT_APPROVED`.
- Generated artifacts record `distributionVersion`.
- **Decision:** E6 `PUT /cabinet-plan` and `POST /cabinet-plan` (generator)
  return `409 DISTRIBUTION_MANAGED` when the session has any distribution;
  `GET /cabinet-plan` keeps working (returns the materialized plan). Sessions
  without distributions keep the E6 behavior (legacy).

## Acceptance Criteria

- [ ] Approval with unacknowledged ERRORs is rejected (`422`).
- [ ] Approval creates cabinets matching the distribution items and X.
- [ ] Only one approved version per session.
- [ ] CSV/Fusion/prototype endpoints return `409` without an approved version.
- [ ] Artifacts store the distribution version they came from.
- [ ] E6 `PUT/POST /cabinet-plan` return `409` for sessions with a distribution; `GET` still works.

## Test Plan

- Service ITs: materialization mapping, supersede, rollback; resource ITs for the gate on CSV/Fusion/prototype endpoints.
