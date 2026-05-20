# Spec-Driven Development

Kalitron Furniture Studio uses Spec-Driven Development for product and
integration work. A spec is a short, versioned agreement about what will be
built before code changes start.

## Goals

- Make GitHub issues implementable without hidden assumptions.
- Keep Studio, the AI Gateway, and Fusion 360 export contracts aligned.
- Turn acceptance criteria into explicit tests and manual verification steps.
- Preserve product decisions close to the codebase.

## Workflow

1. Create a branch for the issue or documentation change.
2. Add or update the spec under `docs/specs/eN-feature-name/`.
3. Keep the spec in `Draft` while requirements are still changing.
4. Review API contracts, data model changes, UX states, security, failures, and tests.
5. Move the spec to `Reviewed`.
6. Implement the issue.
7. Update the spec if the implementation changes the contract.
8. Move the spec to `Implemented` when the PR is ready.

## Status Values

| Status       | Meaning                                              |
| ------------ | ---------------------------------------------------- |
| Draft        | Requirements are being shaped. Do not implement yet. |
| Reviewed     | Scope and contracts are stable enough to build.      |
| Implementing | Code is in progress against this spec.               |
| Implemented  | Code and verification are complete.                  |

## Directory Layout

```text
docs/specs/
  README.md
  templates/
    feature-spec-template.md
    api-contract-template.md
  e2-ai-design-chat/
    epic.md
    08-chat-session.md
    09-reference-photo-upload.md
    10-catalog-style-selection.md
    11-chat-gateway-proxy.md
    12-seed-catalog-styles.md
  e6-measured-layout-cabinet-planning/
    epic.md
    39-measured-room-layout.md
    40-layout-zones-obstacles.md
    41-layout-cabinet-planning-contracts.md
```

## Rules

- One GitHub issue should usually have one spec.
- Specs must describe observable behavior, not implementation wishes.
- API contracts must include auth, request, response, validation, and errors.
- Data model changes must name affected entities and persistence rules.
- Database changes must state whether the migration is new or corrective.
- Corrective database changes require a new Liquibase changeset; never edit a
  changeset that may already have run in any local, CI, staging, or production database.
- Frontend specs must cover loading, empty, error, success, and mobile states.
- AI-related specs must separate deterministic Studio behavior from AI Gateway behavior.
- Pull requests must link the spec file and the GitHub issue.
