# E2 Issue 8: Start a Design Session and Chat With the AI Designer

Status: Implementing
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

- [x] Client fills name and email to start a session.
- [x] AI greets the client and asks whether the project is kitchen, closet, or both.
- [x] Messages appear without a full page reload.
- [x] AI asks a maximum of 2 questions per message.
- [x] Session is saved with a unique code such as `KD-2026-001`.
- [x] Client can resume the session after closing the browser.
- [x] Loading indicator appears while the AI is thinking.
- [ ] UI works at 375px width.

## API Contract

### Start session

`POST /api/chat/sessions`

Auth: JWT required.

Request:

```json
{
  "clientName": "Ana Lopez",
  "clientEmail": "ana@example.com"
}
```

Response:

```json
{
  "sessionId": 1,
  "sessionCode": "KD-2026-001",
  "clientName": "Ana Lopez",
  "clientEmail": "ana@example.com",
  "projectType": "KITCHEN",
  "status": "CHATTING",
  "messages": [
    {
      "role": "ASSISTANT",
      "content": "Hola Ana Lopez, soy tu diseñador IA de Kalitron. ¿Tu proyecto es cocina, closet o ambos?",
      "createdAt": "2026-05-11T00:00:00Z"
    }
  ]
}
```

### Resume session

`GET /api/chat/sessions/{sessionCode}`

Auth: JWT required.

Response: same shape as start session.

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
- This issue does not change the generated JDL domain model.
- Initial session creation uses the required `DesignSession.projectType` field with a temporary operational default.
- A later message can update `projectType` when the user answers kitchen, closet, or both.

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
- For #8, assistant replies are deterministic Studio placeholders.
- #11 replaces the placeholder assistant response with the AI Gateway proxy.

## Test Plan

- Backend integration test for authenticated message send.
- Backend test verifies user and assistant messages are persisted.
- Frontend test verifies loading and rendered reply.
- Manual check at 375px viewport.

## Decisions

- First-session creation uses `POST /api/chat/sessions`.
- Resume uses `sessionCode`; the browser stores the latest session code in local storage.
- `POST /api/chat/message` remains JWT-authenticated and receives a numeric `sessionId`.
