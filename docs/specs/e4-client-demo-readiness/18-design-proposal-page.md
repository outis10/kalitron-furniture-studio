# E4 Issue 18: Design Proposal Page

Status: Done
Issue: #18
Epic: #17

## Problem

Designers need a polished proposal page they can show or share with a client
after the AI design conversation and visual concept generation.

## Goal

Provide a public proposal page for a design session that shows the generated
concept, design summary, cabinet count, and next steps with print/PDF and
WhatsApp sharing affordances.

## Acceptance Criteria

- [x] Proposal page shows full-size render, kitchen specs summary, and cabinet count.
- [x] Download PDF button generates a shareable document.
- [x] Shareable link works without login.
- [x] WhatsApp share button is available for mobile clients.
- [x] Print-friendly CSS is applied.

## Implementation Notes

- MVP uses browser print/save-as-PDF instead of server-side PDF generation.
- Public access is by session code: `/proposal/{sessionCode}`.
- API returns only proposal-safe data, not raw chat history or client email.
- Cabinet count is derived from persisted `Cabinet` rows when a `KitchenSpec`
  exists; otherwise it is `0`.

## Test Plan

- [x] Backend integration test verifies public proposal access without login.
- [x] Backend integration test verifies `404` for unknown session code.
- [x] Frontend build validates route and page wiring.
- [ ] Manual test print/PDF and WhatsApp link.

## Validation Notes

- `./npmw run webapp:build:dev -- --env stats=minimal` passed.
- `./gradlew integrationTest --tests com.kalitron.studio.web.rest.custom.ProposalResourceIT -x webapp -x webapp_test` passed.
