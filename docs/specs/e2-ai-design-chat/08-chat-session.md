# E2 Issue 8: Start a Design Session and Chat With the AI Designer

Status: Draft
Issue: #8
Epic: #7

## Problem

A prospective client needs to start a guided design conversation without knowing
technical kitchen design terminology.

## Goal

Provide a responsive chat flow where the client starts a session, exchanges
messages with the AI designer, and can resume the conversation after closing or
refreshing the browser.

## Acceptance Criteria

- [ ] Client fills name and email to start a session.
- [ ] AI greets the client and asks whether the project is kitchen, closet, or both.
- [ ] Messages appear without a full page reload.
- [ ] AI asks a maximum of 2 questions per message.
- [ ] Session is saved with a unique code such as `KD-2026-001`.
- [ ] Client can resume the session after closing the browser.
- [ ] Loading indicator appears while the AI is thinking.
- [ ] UI works at 375px width.

## API Contract

### Send message

`POST /api/chat/message`

Auth: JWT required.

Request:

```json
{
  "sessionId": 1,
  "message": "Quiero una cocina moderna blanca",
  "imageBase64": null
}
```

Response:

```json
{
  "sessionId": 1,
  "reply": "Perfecto. Para orientarte mejor, ¿tu cocina es lineal, en L o en U?",
  "specsReady": false,
  "specsSummary": null
}
```

## Data Model Impact

- Create or update `DesignSession`.
- Persist each user and assistant message in `ChatMessage`.
- Use `DesignSession.sessionCode` as the resumable business identifier.
- Do not store image base64 in `ChatMessage`.

## Frontend Behavior

- Chat module lives under `src/main/webapp/app/modules/chat/`.
- API calls live under `src/main/webapp/app/shared/api/`.
- Show loading state while waiting for the backend.
- Preserve current session identifier in browser storage.
- Render empty, error, retry, and success states.

## Backend Behavior

- Custom endpoint belongs under `web/rest/custom/`.
- Resource delegates to service; no business logic in controller.
- Save user message before calling AI Gateway.
- Save assistant reply after successful gateway response.
- Return graceful error when the gateway is unavailable.

## Test Plan

- Backend integration test for authenticated message send.
- Backend test verifies user and assistant messages are persisted.
- Frontend test verifies loading and rendered reply.
- Manual check at 375px viewport.

## Open Questions

- [ ] How should first-session creation be exposed if `POST /api/chat/message` requires an existing `sessionId`?
- [ ] Should resume use `sessionCode`, local storage, or both?
