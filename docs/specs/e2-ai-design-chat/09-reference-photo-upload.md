# E2 Issue 9: Upload a Reference Photo

Status: Implementing
Issue: #9
Epic: #7

## Problem

Clients often have an existing kitchen and need the AI designer to understand the
current space from a photo.

## Goal

Allow clients to attach a reference photo in the chat, preview it, send it to the
AI Gateway, and persist image metadata in Studio.

## Acceptance Criteria

- [x] Chat input includes an upload button.
- [x] Drag and drop is supported.
- [x] Accepted types: JPG, PNG, WebP.
- [x] Maximum file size is 5MB.
- [x] Inline image preview appears in chat.
- [x] Image is sent as base64 in the chat request for AI Gateway forwarding.
- [x] AI acknowledges the image and comments on the space.
- [x] Invalid type or size shows a clear error.
- [x] Image metadata is saved to `DesignImage` with type `REFERENCE`.

## Data Model Impact

- Create `DesignImage` record with `imageType=REFERENCE`.
- Store file path or URL, MIME type, and dimensions when available.
- Do not store base64 in the database.

## Frontend Behavior

- Validate type and size before upload.
- Show selected image preview before send.
- Keep chat layout usable at 375px.
- Allow removing selected image before sending.

## Backend Behavior

- Accept image payload only as part of chat message request for Gateway forwarding.
- Persist metadata separately from chat text.
- Reject invalid files if frontend validation is bypassed.

## AI Gateway Contract

- Studio sends base64 only in the request to AI Gateway.
- AI Gateway returns a normal chat reply plus any extracted observations.
- Studio decides what metadata is stored.

## Implementation Notes

- MVP stores `DesignImage` metadata only. The `filePath` is a logical reference
  path; base64 is never persisted in the database.
- The current Studio chat service accepts image payloads and produces a temporary
  acknowledgement. Issue #11 will replace that temporary response with the real
  FastAPI Gateway proxy.
- The request carries `imageBase64`, `imageFileName`, `imageMimeType`, and
  `imageSizeBytes`; backend validation repeats frontend validation.

## Test Plan

- [ ] Frontend tests for type and size validation.
- [ ] Frontend test for preview and remove behavior.
- [x] Backend test for `DesignImage` creation.
- [ ] Manual test with JPG, PNG, WebP, oversized file, and invalid type.

## Open Questions

- [x] Where should MVP images be stored: metadata only with logical `filePath`; object storage is deferred.
- [x] Should image upload require the chat endpoint or have a separate upload endpoint? Use chat endpoint for MVP.
