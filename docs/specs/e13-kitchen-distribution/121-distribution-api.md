# [E13] Issue 121: Distribution API — Versions and Idempotent Mobile v0 Sync

Status: Draft
Issue: #121
Epic: #118
Related: #119 (model), #122 (validation), #123 (editor), outis10/KFS-APP#24 (v0 sync), E12 #112 (same sync semantics)
Owner: TBD

## Goal

Endpoints to sync the v0 from the app (idempotent, versioned) and to manage
versions in Studio (copy, edit, validate, acknowledge, discard).

## API Contract

Base: `/api/design-sessions/{sessionId}/distributions`

### Mobile v0 upsert

`PUT …/by-uuid/{distributionUuid}`

- Auth: `ROLE_ADMIN` or assigned `ROLE_MEASURER` (E12 #116).
- Body: E13 distribution JSON + `revision`, `baseRevision`, `source: MOBILE_VISIT`.
- Semantics identical to E12 #112: create `201` (assigns `versionNumber` 0 if
  free, else next number), replay `200`, stale `baseRevision` `409`,
  locked (already copied by Studio) `409 DISTRIBUTION_LOCKED`.
- Requires the referenced `measurementUuid` to exist in Studio (app syncs the
  measurement first) → else `422 MEASUREMENT_NOT_SYNCED`.
- Response: `{ versionNumber, status: "PRELIMINARY", revision, validationIssues[] }`.

### Studio versions

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `…/distributions` | List versions: number, status, source, label, counts, dates, author |
| `GET` | `…/distributions/{version}` | Full payload + derived X per item + `validationIssues[]` |
| `POST` | `…/distributions` `{ "fromVersion": 0, "label": "v1" }` | Copy any version into a new `DRAFT` (rebased on the latest confirmed measurement) |
| `PUT` | `…/distributions/{version}` | Replace payload of a `DRAFT` (`If-Match` with `updatedAt`/ETag; `412` on mismatch) |
| `POST` | `…/distributions/{version}/validate` | Dry-run validation of an unsaved payload (editor live validation) |
| `POST` | `…/distributions/{version}/acknowledgements` `{ ruleCode, itemUuid, reason }` | Acknowledge an acknowledgeable ERROR |
| `POST` | `…/distributions/{version}/discard` | Mark non-approved version `DISCARDED` |

- Studio auth: `ROLE_ADMIN` or `ROLE_DESIGNER` with session visibility (#126).
- Every response with a payload includes derived `xMm` per item and
  authoritative `validationIssues[]`.

### Errors

| Status | Cause |
| --- | --- |
| 400 | Invalid payload, unsupported `schemaVersion`, negative widths |
| 403 | Role / not assigned |
| 404 | Session or version not found |
| 409 | Revision conflict, locked v0, immutable status |
| 412 | Stale `If-Match` in Studio editor |
| 422 | Measurement not synced |

## Backend Behavior

- Resource `web/rest/custom/DistributionResource`; service
  `LayoutDistributionService` (+ Impl); validation via #122.
- Each write in one transaction; `versionNumber` allocated with a session-level lock.
- No business logic in the resource.

## Acceptance Criteria

- [ ] Replayed mobile upsert creates no duplicates.
- [ ] Copying v0 creates v1 `DRAFT` and locks v0 for the app.
- [ ] Editing an `APPROVED`/`SUPERSEDED` version is rejected.
- [ ] Validate endpoint returns in < 300 ms for a 4-wall kitchen.

## Test Plan

- Resource ITs per endpoint and error; concurrency test for version allocation.
