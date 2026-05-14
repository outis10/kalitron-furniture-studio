# E2 Issue 11: ChatResource Proxy to AI Gateway

Status: Implementing
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

- [x] `POST /api/chat/message` requires JWT authentication.
- [x] Endpoint proxies to FastAPI `POST /api/v1/chat/message` using `RestClient`.
- [x] User message and AI reply are saved to `ChatMessage`.
- [x] `DesignSession.status` updates when `specsReady=true`.
- [x] Gateway base URL is configurable through `app.fastapi.base-url`.
- [x] FastAPI unavailable returns a graceful error response.

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

Current AI Gateway schema accepts `session_id`, `message`, and `image_b64`.
Studio sends `DesignSession.sessionCode` as `session_id`. Selected style context
is included in the forwarded message text until the gateway schema grows a
dedicated style field.

## Backend Behavior

- `ChatResource` validates request and delegates to service.
- Service loads `DesignSession`.
- Service persists user `ChatMessage`.
- `FastApiGateway` calls FastAPI via `RestClient`.
- Service persists assistant `ChatMessage`.
- If `specsReady=true`, set session status to `SPECS_READY`.
- If Gateway fails, return a client-safe assistant reply without losing the user message.

## Failure Cases

- Missing auth: `401`.
- Missing session: `404`.
- Empty message and no image: `400`.
- Gateway timeout/unavailable: `200` with a client-safe assistant reply so the
  chat remains usable and the user message stays persisted.

## Test Plan

- [x] Backend integration test for authentication requirement.
- [x] Backend integration test for successful proxy and persistence.
- [x] Backend test for gateway unavailable.
- [x] Backend test for `SPECS_READY` transition.

## Open Questions

- [x] Should the service save the user message if the Gateway call fails? Yes;
      the user message is persisted before calling the gateway.
- [x] Should Gateway timeout use the existing `app.ai-gateway.timeout-seconds` property? Yes.
