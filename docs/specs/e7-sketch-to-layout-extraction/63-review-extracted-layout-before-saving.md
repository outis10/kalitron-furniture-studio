# E7 Issue 63: Review Extracted Layout Before Saving

Status: Implemented
Issue: #63
Epic: #57
Depends on: #58, #59, #60

## Problem

Sketch extraction is AI-assisted and can be incomplete or uncertain. Studio must
not turn extracted sketch data into confirmed layout or cabinet data without
human review.

## Goal

Show extracted sketch data in an editable review panel before later issues
persist it as measured layout or cabinet plan data.

## Acceptance Criteria

- [x] UI shows extracted layout, walls, zones, obstacles, and measurements.
- [x] UI shows confidence per extracted item.
- [x] Low-confidence and missing fields are visually distinct.
- [x] User can edit extracted values before confirming.
- [x] User can reject extraction and return to manual layout capture.
- [x] Confirmation is required before saving to measured layout or cabinet plan.

## User Flow

1. User uploads and analyzes a sketch from the chat.
2. Studio shows an extraction summary in the chat history.
3. Studio opens a review panel with editable extracted fields.
4. User corrects project type, layout, walls, zones, obstacles, and cabinet
   candidates as needed.
5. User confirms the extraction as a reviewed draft or discards it.
6. Manual layout capture remains available from the review panel.

## UX Rules

- The review panel must call the result a draft.
- `LOW` and `MISSING` confidence fields must stand out visually.
- Confirming the review must not persist measured layout or cabinet plan data in
  this issue.
- Discarding the review clears the extracted draft and guides the user to manual
  layout capture.

## Out of Scope

- Persisting reviewed extraction into `MeasuredLayoutRequestDTO`. Covered by #62.
- Persisting reviewed cabinet candidates into editable cabinet plan. Covered by
  #61.
- Gateway model accuracy improvements.

## Manual Verification

1. Start a chat session.
2. Upload a sketch and select `Analizar`.
3. Confirm the review panel appears after the extraction response.
4. Confirm project/layout/unit fields are editable.
5. Confirm walls, zones, obstacles, and cabinet candidates are shown when
   returned by the gateway.
6. Confirm `LOW` or `MISSING` fields are visually distinct.
7. Edit one value and confirm the panel returns to pending state.
8. Confirm the extraction; verify it is labeled as reviewed draft only.
9. Discard the extraction; verify manual layout capture remains available.
