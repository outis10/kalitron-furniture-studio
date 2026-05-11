# E2 Issue 9: Upload a Reference Photo

Status: Draft
Issue: #9
Epic: #7

## Problem

Clients often have an existing kitchen and need the AI designer to understand the
current space from a photo.

## Goal

Allow clients to attach a reference photo in the chat, preview it, send it to the
AI Gateway, and persist image metadata in Studio.

## Acceptance Criteria

- [ ] Chat input includes an upload button.
- [ ] Drag and drop is supported.
- [ ] Accepted types: JPG, PNG, WebP.
- [ ] Maximum file size is 5MB.
- [ ] Inline image preview appears in chat.
- [ ] Image is sent as base64 to the AI Gateway.
- [ ] AI acknowledges the image and comments on the space.
- [ ] Invalid type or size shows a clear error.
- [ ] Image metadata is saved to `DesignImage` with type `REFERENCE`.

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

## Test Plan

- Frontend tests for type and size validation.
- Frontend test for preview and remove behavior.
- Backend test for `DesignImage` creation.
- Manual test with JPG, PNG, WebP, oversized file, and invalid type.

## Open Questions

- [ ] Where should MVP images be stored: local output directory, database-adjacent filesystem, or object storage abstraction?
- [ ] Should image upload require the chat endpoint or have a separate upload endpoint?
