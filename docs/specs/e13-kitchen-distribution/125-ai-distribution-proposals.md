# [E13] Issue 125: Request AI Distribution Proposals from the Gateway

Status: Draft
Issue: #125
Epic: #118
Gateway: outis10/kalitron-furniture-ai-gateway#40 (contract), #41 (endpoint)
Related: #120 (library), #122 (validation), #123 (editor)
Owner: TBD

## Goal

From the confirmed measurement, the client interview and the module library,
ask the gateway for 2–3 distributions, validate them in Studio and store them
as `PROPOSED` versions the designer can compare and copy.

## Inputs Studio sends (contract gw#40)

- Measurement: walls (design length, corners, out-of-plumb), elements with
  positions (services, windows, doors, beams, hood), ceiling height.
- Interview: `KitchenSpec` fields, selected style, chat summary (E2), v0
  `clientNotes`, optional designer instructions ("isla no", "más cajones").
- Library: active modules and appliance slots (#120).
- Rule params: clearances/tolerances from the catalog (so proposals aim for them).
- Optional: an existing version as starting point (e.g. v0).
- `count`: 2–3.
- `allowFreestanding`: whether islands/peninsulas may be proposed (#128), plus room geometry (wall start points/angles).

## Flow

1. Designer (`ROLE_ADMIN`/`ROLE_DESIGNER`) clicks "Proponer distribuciones (IA)" → `POST /api/design-sessions/{id}/distributions/proposals` `{ count, instructions, fromVersion? }`.
2. Studio creates `GenerationJob` (`DISTRIBUTION_PROPOSAL`) → `202 { jobId }`.
3. Async call to gateway; timeout `app.ai-gateway.timeout-seconds`.
4. Each proposal validated (#122). If **all** have unacknowledgeable ERRORs,
   one repair retry sending the issues back (`previousIssues`).
5. Stored as `PROPOSED` versions (`source AI_PROPOSAL`, label + rationale in payload).
6. Job `COMPLETED` / `FAILED` with message; editor polls job status.

## Rules

- Proposals never become `APPROVED` directly without passing #124.
- Proposals with ERRORs are still shown (with issues) so the designer can fix them.
- No client email/phone sent to the gateway.

## Acceptance Criteria

- [ ] 2–3 `PROPOSED` versions created per successful job, each with rationale and issues.
- [ ] Gateway failure → job `FAILED`, no partial versions.
- [ ] No contact data in the gateway request (test).

## Test Plan

- Service tests with mocked gateway (valid, invalid → repair, failure, timeout).
