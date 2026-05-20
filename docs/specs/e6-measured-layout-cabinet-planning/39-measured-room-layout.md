# E6 Issue 39: Measured Room Layout

Status: Done
Issue: #39
Epic: #38

## Problem

Cabinet planning needs real room measurements. A reference photo can guide style
and structure, but it cannot be trusted for fabrication dimensions.

## Goal

Let a designer choose a guided layout type, enter wall lengths and room height,
see a simple 2D preview, save normalized measurements, and reopen the layout
from an existing design session.

## Acceptance Criteria

- [x] Designer can choose layout type: linear, L, U, or island.
- [x] Designer can enter wall segment lengths and room height.
- [x] UI shows a simple 2D preview of the layout.
- [x] Measurements are stored in normalized backend units.
- [x] Layout can be reopened from the design session.
- [x] UI works at desktop and tablet widths.

## Implementation Notes

- UI captures measurements in centimeters for designer ergonomics.
- API stores and returns millimeters through `MeasuredLayoutRequestDTO`.
- `RoomWall` persists normalized wall segments.
- `DesignArtifact` stores an inline JSON snapshot named
  `measured-layout.json` so layout type, defaults, notes, and future fields can
  be reopened without adding a schema migration in this issue.
- This is a guided parametric layout, not freehand CAD.

## API

```http
PUT /api/design-sessions/{sessionId}/measured-layout
GET /api/design-sessions/{sessionId}/measured-layout
```

Both endpoints require authentication.

## Test Plan

- [x] Integration test verifies authentication is required.
- [x] Integration test saves normalized walls and reopens the measured layout.
- [x] Integration test rejects an L-shaped layout with only one wall.
- [x] `./gradlew integrationTest --tests com.kalitron.studio.web.rest.custom.MeasuredLayoutResourceIT -x webapp -x webapp_test`
- [ ] Manual tablet-width UI test.
