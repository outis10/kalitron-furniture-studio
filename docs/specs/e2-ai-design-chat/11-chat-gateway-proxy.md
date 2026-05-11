# E2 Issue 11: ChatResource Proxy to AI Gateway

Status: Draft
Issue: #11
Epic: #7

## Problem

Studio needs a secure backend endpoint that stores chat history and delegates AI
responses to the FastAPI AI Gateway.

## Goal

Implement `POST /api/chat/message` as an authenticated Studio endpoint that
proxies to the AI Gateway, persists messages, and updates session status when
specs are ready.

## Acceptance Criteria

- [ ] `POST /api/chat/message` requires JWT authentication.
- [ ] Endpoint proxies to FastAPI `POST /api/v1/chat/message` using `RestClient`.
- [ ] User message and AI reply are saved to `ChatMessage`.
- [ ] `DesignSession.status` updates when `specsReady=true`.
- [ ] Gateway base URL is configurable through `app.fastapi.base-url`.
- [ ] FastAPI unavailable returns a graceful error response.

## Files

- `src/main/java/com/kalitron/studio/web/rest/custom/ChatResource.java`
- `src/main/java/com/kalitron/studio/service/FastApiGateway.java`
- `src/main/java/com/kalitron/studio/service/dto/ChatRequestDTO.java`
- `src/main/java/com/kalitron/studio/service/dto/ChatResponseDTO.java`

## API Contract

### Studio endpoint

`POST /api/chat/message`

Auth: JWT required.

Request:

```json
{
  "sessionId": 1,
  "message": "Tengo una cocina en L de 3 por 2 metros",
  "imageBase64": null
}
```

Response:

```json
{
  "sessionId": 1,
  "reply": "Gracias. Para avanzar, ¿prefieres acabado blanco, madera o gris?",
  "specsReady": false,
  "specsSummary": null
}
```

### AI Gateway endpoint

`POST {app.fastapi.base-url}/api/v1/chat/message`

Studio forwards session context, latest user message, optional image base64, and
selected style context when present.

## Backend Behavior

- `ChatResource` validates request and delegates to service.
- Service loads `DesignSession`.
- Service persists user `ChatMessage`.
- `FastApiGateway` calls FastAPI via `RestClient`.
- Service persists assistant `ChatMessage`.
- If `specsReady=true`, set session status to `SPECS_READY`.
- If Gateway fails, return a client-safe error without losing the user message.

## Failure Cases

- Missing auth: `401`.
- Missing session: `404`.
- Empty message and no image: `400`.
- Gateway timeout/unavailable: graceful error response.

## Test Plan

- Backend integration test for authentication requirement.
- Backend integration test for successful proxy and persistence.
- Backend test for gateway unavailable.
- Backend test for `SPECS_READY` transition.

## Open Questions

- [ ] Should the service save the user message if the Gateway call fails?
- [ ] Should Gateway timeout use the existing `app.ai-gateway.timeout-seconds` property?
