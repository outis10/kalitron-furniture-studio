# E2 AI-Powered Design Conversation Flow

Status: Draft
Issue: #7
Milestone: v0.2 - Design Chat & Conversation

## Problem

Clients need a guided way to describe a kitchen or closet project without knowing
design, cabinetry, or manufacturing terminology. The system must turn that
conversation into structured design data that can later support visual concepts,
BOM, quote, and Fusion 360 artifacts.

## Goal

Deliver a multi-turn AI design conversation that persists across refreshes,
supports optional reference photos, captures selected style context, and extracts
layout, dimensions, style, finish, and cabinet needs into the Studio domain model.

## Definition of Done

- [ ] Client can complete design discovery in under 5 minutes.
- [ ] AI extracts layout, dimensions, style, finish, and cabinet types.
- [ ] Session persists across page refreshes.
- [ ] Flow works with and without an uploaded photo.

## Scope

- Chat session start and resume.
- AI Gateway proxy endpoint.
- Reference photo upload from chat.
- Catalog style selection before conversation.
- Seed catalog styles.

## Non-Goals

- Photorealistic render generation.
- BOM generation.
- Quote generation.
- Fusion 360 CSV or script export.
- Public shareable proposal pages.

## Issue Specs

- [#8 Chat session](08-chat-session.md)
- [#9 Reference photo upload](09-reference-photo-upload.md)
- [#10 Catalog style selection](10-catalog-style-selection.md)
- [#11 Chat gateway proxy](11-chat-gateway-proxy.md)
- [#12 Seed catalog styles](12-seed-catalog-styles.md)

## Architecture Notes

Studio owns authentication, session persistence, chat history, image metadata,
catalog style context, and domain status transitions. The AI Gateway owns language
model interaction, image interpretation, and structured extraction suggestions.
Manufacturing truth remains in validated Studio domain entities, not generated
chat text or visual concepts.

## Open Questions

- [ ] Should anonymous prospective clients use JWT accounts, a temporary session token, or a public start-session endpoint?
- [ ] Should uploaded reference photos be stored locally for MVP or behind an object storage abstraction from day one?
- [ ] What exact schema should `specsSummary` use when returned from the AI Gateway?
