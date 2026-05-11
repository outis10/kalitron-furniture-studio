# E2 Issue 10: Browse Catalog Styles

Status: Draft
Issue: #10
Epic: #7

## Problem

Clients may not know how to describe a design style in words.

## Goal

Show visual catalog style cards before the first chat message so the selected
style can guide the AI conversation.

## Acceptance Criteria

- [ ] Style cards appear at session start before the first message.
- [ ] Each card shows thumbnail, style name, price range badge, and description.
- [ ] Client can select one style or skip.
- [ ] Selected style is sent as context to the AI in every message.
- [ ] Styles load from a public endpoint with no JWT required.
- [ ] At least 6 styles are preloaded: Moderno, Minimalista, Rustico, Clasico, Industrial, Escandinavo.

## API Contract

`GET /api/catalog-styles`

Auth: public endpoint.

Response: paginated or array response using the generated JHipster DTO shape.

## Data Model Impact

- Uses existing `CatalogStyle`.
- Only active styles should be shown to clients.

## Frontend Behavior

- Style picker appears before the first chat exchange.
- Cards must be scannable and mobile friendly.
- Skip is explicit and does not block chat start.
- Selected style remains visible as context.

## Backend Behavior

- Endpoint must be public in security configuration.
- Avoid returning inactive styles in the public experience.

## Test Plan

- Backend test for unauthenticated access.
- Frontend test for select and skip.
- Manual responsive check at 375px.

## Open Questions

- [ ] Should the public endpoint return all generated fields or a smaller public DTO?
- [ ] Should selected style be stored on `DesignSession`?
