# E2 Issue 12: Seed CatalogStyle Table

Status: Implementing
Issue: #12
Epic: #7

## Problem

The style selection flow needs real catalog data before the frontend can present
style cards.

## Goal

Seed `CatalogStyle` with 8 active design styles and make them available through
the public catalog styles endpoint.

## Acceptance Criteria

- [x] Add Liquibase changelog `src/main/resources/config/liquibase/changelog/20250601_seed_catalog_styles.xml`.
- [x] Insert all 8 styles with `is_active=true`.
- [x] `GET /api/catalog-styles` returns all 8 without authentication.
- [x] Thumbnail placeholder paths are set.

## Seed Data

| Name              | Style       | Price Range |
| ----------------- | ----------- | ----------- |
| Moderno Blanco    | moderno     | medio       |
| Moderno Gris      | moderno     | medio       |
| Minimalista Negro | minimalista | premium     |
| Madera Natural    | rustico     | medio       |
| Rustico Pino      | rustico     | economico   |
| Clasico Crema     | clasico     | premium     |
| Industrial        | industrial  | medio       |
| Escandinavo       | minimalista | medio       |

## Data Model Impact

- Uses existing `CatalogStyle`.
- No new entity fields are expected.

## Backend Behavior

- Add a Liquibase changelog in `changelog/`.
- The project uses explicit Liquibase includes, so `master.xml` must include the seed changelog.
- The seed changelog removes generated faker catalog rows with IDs `1..10` before inserting the real styles.
- Public access to `GET /api/catalog-styles` must be configured in security.

## Test Plan

- Backend integration test confirms unauthenticated catalog access.
- Liquibase migration runs on PostgreSQL.
- Manual API call returns 8 active styles.

## Decisions

- JHipster 9 did not auto-include this hand-written changelog; it is included explicitly in `master.xml`.
- Placeholder thumbnail paths use `/content/images/catalog-styles/*.webp`.
