# E4 Issue 19: Complete Design Flow E2E

Status: Done
Issue: #19
Epic: #17

## Problem

The demo flow spans authentication, style selection, AI chat, visual concept
generation, and the public proposal page. Regressions across these boundaries
are hard to catch with isolated tests.

## Goal

Add a Cypress happy-path test that exercises the complete client demo flow while
mocking backend AI responses so it can run predictably in CI.

## Acceptance Criteria

- [x] Test runs in CI with AI Gateway mocked.
- [x] Completes in under 2 minutes.
- [x] Screenshots saved on failure.
- [x] Test data cleaned up after run.

## Implementation Notes

- Cypress intercepts the Studio API endpoints used by the flow, including chat,
  visual concept generation, and proposal loading.
- The AI Gateway is mocked through the `/api/chat/visual-concept` response.
- The test clears browser local/session storage before and after each run.
- Screenshots use Cypress default failure behavior and are written under
  `build/cypress/screenshots`.

## Test Plan

- [x] `./npmw run lint`
- [x] `./npmw run webapp:build:dev -- --env stats=minimal`
- [x] `./npmw run e2e:run -- --spec cypress/e2e/complete-design-flow.cy.ts` with the app available at `http://localhost:9000`

## Validation Notes

- Cypress completed the flow in 11 seconds using mocked Studio API responses for
  chat, visual concept generation, and public proposal loading.
