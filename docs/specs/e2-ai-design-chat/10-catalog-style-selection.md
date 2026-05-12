# E2 Issue 10: Browse Catalog Styles

Status: Implementing
Issue: #10
Epic: #7

## Problem

Clients may not know how to describe a design style in words.

## Goal

Show visual catalog style cards before the first chat message so the selected
style can guide the AI conversation.

## Acceptance Criteria

- [x] Style cards appear at session start before the first message.
- [x] Each card shows thumbnail, style name, price range badge, and description.
- [x] Client can select one style or skip.
- [x] Selected style is sent as context to the AI in every message.
- [x] Styles load from a public endpoint with no JWT required.
- [x] At least 6 styles are preloaded: Moderno, Minimalista, Rustico, Clasico, Industrial, Escandinavo.

## API Contract

`GET /api/catalog-styles`

Auth: public endpoint.

Response: paginated or array response using the generated JHipster DTO shape.

## Data Model Impact

- Uses existing `CatalogStyle`.
- Only active styles should be shown to clients.
- Selected style is stored on `DesignSession.selectedStyle` as the selected catalog style name.

## Frontend Behavior

- Style picker appears before the first chat exchange.
- Cards must be scannable and mobile friendly.
- Skip is explicit and does not block chat start.
- Selected style remains visible as context.

## Backend Behavior

- Endpoint must be public in security configuration.
- Avoid returning inactive styles in the public experience.
- Chat session start accepts optional `selectedStyle`.
- Chat message send accepts optional `selectedStyle` so the later AI Gateway proxy can forward style context.

## Test Plan

- Backend test for unauthenticated access.
- Frontend test for select and skip.
- Manual responsive check at 375px.

## Decisions

- The MVP uses the generated `CatalogStyleDTO` shape from `GET /api/catalog-styles`.
- The frontend filters inactive styles defensively.
- Selected style is persisted on `DesignSession.selectedStyle`; a future relationship can be added if analytics or catalog integrity require it.
