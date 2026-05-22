# E7 Issue 89: Load Persisted Layout and Sketch on Session Resume

Status: Implemented
Issue: #89
Epic: #57
Depends on: #87, #62, #61

## Problem

After saving a measured layout, cabinet plan, or sketch image, resuming the
session showed no indication of the previously saved data. Users had no way to
view, verify, or re-edit what was already persisted.

## Goal

When a session is resumed, load the persisted measured layout, cabinet plan, and
sketch image and surface them in the chat UI without requiring a new analysis.

## Acceptance Criteria

- [x] On session resume, the sidebar shows a summary of saved artifacts (wall
  count, zone count, cabinet count).
- [x] On session resume, the saved sketch thumbnail is shown in the sidebar.
  Clicking it opens the full image in a new tab.
- [x] A "Ver / Editar guardado" button appears in the sidebar when a saved layout
  exists and no sketch review is currently open. Clicking it loads the persisted
  data back into the sketch review form for editing.
- [x] All fields loaded from persisted data are marked HIGH confidence since they
  were previously user-validated.
- [x] Loading artifacts on resume is silent — errors do not block the session
  from opening.
- [x] Saving a new layout or cabinet plan updates the in-memory objects so
  Ver / Editar guardado reflects the latest saved state without a page reload.

## API Contracts

### GET /api/design-sessions/{sessionId}/sketch-image

- Auth: required (JWT)
- 200: image bytes with correct Content-Type (image/jpeg, image/png, image/webp)
- 204: no sketch image saved for this session

### GET /api/design-sessions/{sessionId}/measured-layout

- Auth: required
- 200: MeasuredLayout JSON
- 204: no layout saved (changed from 404 to avoid triggering error toasts)

### GET /api/design-sessions/{sessionId}/cabinet-plan

- Auth: required
- 200: CabinetPlan JSON
- 204: no cabinet plan saved (changed from 404 to avoid triggering error toasts)

## Persistence Rules

- Sketch image is served by reading the file from `app.output.dir` using the
  `filePath` stored in the `DesignImage` record.
- The blob URL created from the sketch image response must be revoked on session
  restart to avoid memory leaks.
- The converter `savedDataToSketchReview` maps mm values back to string fields
  with unit MM so the review form remains editable after load.

## Manual Verification

1. Save a measured layout and cabinet plan from the sketch review flow.
2. Reload or navigate away, then return to the same session via session code.
3. Confirm the sidebar shows wall count, zone count, and cabinet count.
4. Confirm the sketch thumbnail appears. Click it and verify the full image opens.
5. Click "Ver / Editar guardado" and confirm the review form is populated with
   the previously saved walls, zones, obstacles, and cabinets.
6. Confirm all fields show HIGH confidence.
7. Click "Nueva sesión" and confirm indicators and thumbnail are cleared.
