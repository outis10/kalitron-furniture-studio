# [E12] Issue 110: Convert Confirmed Site Measurement to Measured Layout

Status: Draft
Issue: #110
Epic: #104
Depends on: E7 #62 (extraction → measured layout conversion), #107, #112, #113, #116
Related: outis10/KFS-APP#15 (confirm in app)
Owner: TBD

## Goal

When a `SiteMeasurement` is confirmed (#112 confirm endpoint) and Studio's
authoritative validation reports no `ERROR`, project it into the session's
measured layout and set `SessionStatus.MEASURED`.

## Non-Goals

- Cabinet plan generation (E6/E7 #61).

## Mapping

| Measurement field | Studio target |
| --- | --- |
| wall code, three lengths, out-of-plumb, closing measurement, corner angle | `RoomWall` (`lengthMm` = min of lengths) |
| non-appliance element | `RoomObstacle` with `obstacleType` from catalog + `croquisCode` |
| appliance element | `RoomObstacle` `APPLIANCE` + `applianceType` |
| ceiling height | `RoomWall.heightMm` for all walls |
| floor out of level | `KitchenSpec.floorOutOfLevelMm` + note |
| wall photos | already `DesignImage` `SITE_PHOTO` (#112) |

## Backend Behavior

- Service `SiteMeasurementConversionService`, reusing the #62 conversion
  internals; v1 extraction mapping untouched.
- Called inside the confirm transaction of #112: replace the session's
  walls/obstacles, update spec, set `MEASURED`, set measurement `CONFIRMED`.
  Rollback on any failure.
- Idempotent: confirming the same `measurementUuid` + `revision` twice returns
  the same layout without duplicates.

## Acceptance Criteria

- [ ] Confirm with 0 errors creates walls/obstacles with extended fields.
- [ ] Any `ERROR` after server re-validation → `422`, nothing changes.
- [ ] Session status becomes `MEASURED`.
- [ ] v1 conversion tests pass unchanged.

## Test Plan

- Backend: mapper unit tests from conformance fixtures; IT for rollback and idempotency.
