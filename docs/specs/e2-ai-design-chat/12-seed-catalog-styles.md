# E2 Issue 12: Seed CatalogStyle Table

Status: Draft
Issue: #12
Epic: #7

## Problem

The style selection flow needs real catalog data before the frontend can present
style cards.

## Goal

Seed `CatalogStyle` with 8 active design styles and make them available through
the public catalog styles endpoint.

## Acceptance Criteria

- [ ] Add Liquibase changelog `src/main/resources/config/liquibase/changelog/20250601_seed_catalog_styles.xml`.
- [ ] Insert all 8 styles with `is_active=true`.
- [ ] `GET /api/catalog-styles` returns all 8 without authentication.
- [ ] Thumbnail placeholder paths are set.

## Seed Data

| Name | Style | Price Range |
| --- | --- | --- |
| Moderno Blanco | moderno | medio |
| Moderno Gris | moderno | medio |
| Minimalista Negro | minimalista | premium |
| Madera Natural | rustico | medio |
| Rustico Pino | rustico | economico |
| Clasico Crema | clasico | premium |
| Industrial | industrial | medio |
| Escandinavo | minimalista | medio |

## Data Model Impact

- Uses existing `CatalogStyle`.
- No new entity fields are expected.

## Backend Behavior

- Add a Liquibase changelog in `changelog/`.
- Do not manually edit `master.xml` unless the project requires manual inclusion.
- Public access to `GET /api/catalog-styles` must be configured in security.

## Test Plan

- Backend integration test confirms unauthenticated catalog access.
- Liquibase migration runs on PostgreSQL.
- Manual API call returns 8 active styles.

## Open Questions

- [ ] Does JHipster 9 auto-include the new changelog in this project, or must it be added explicitly?
- [ ] Should placeholder thumbnails live under `/content/images/catalog-styles/`?
