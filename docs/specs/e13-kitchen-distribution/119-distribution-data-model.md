# [E13] Issue 119: Versioned Kitchen Distribution Data Model

Status: Draft
Issue: #119
Epic: #118
Related: #121 (API), #124 (approval), E12 #107 (`SiteMeasurement`)
Owner: TBD

## Problem

`Cabinet` rows are a single unversioned snapshot per `KitchenSpec`. The
distribution needs versions (v0 from the app, Studio drafts, AI proposals,
one approved) with traceability to the measurement it was based on.

## Goal

A `LayoutDistribution` entity storing immutable-once-approved snapshots, with
the approved one projected to `Cabinet` rows (#124).

## Data Model Impact

| Entity / enum | Change |
| --- | --- |
| `LayoutDistribution` (new) | `projectType` (`ProjectType`: `KITCHEN` now, `CLOSET` in E14), `distributionUuid` (unique), `versionNumber` (int, per session, 0 = v0), `status` (`DistributionStatus`), `source` (`DistributionSource`), `label` (e.g. "Propuesta IA 2"), `schemaVersion`, `libraryVersion`, `catalogVersion`, `revision` (for mobile sync), `payload` (JSON text: walls/runs/items/acknowledgements/clientNotes), `errorCount`, `warningCount`, `createdAt`, `updatedAt`, `approvedAt`; many-to-one `DesignSession`, `SiteMeasurement` (nullable), `User` createdBy, `User` approvedBy, self `parent` (nullable), `GenerationJob` (nullable, for AI) |
| `DistributionStatus` (new enum) | see *Enums* below |
| `DistributionSource` (new enum) | see *Enums* below |
| `GenerationJobType` | + `DISTRIBUTION_PROPOSAL` (see *Enums*) |
| `Cabinet` | + `wallCode`, `runCode` (islands/peninsulas, #128), `distributionItemUuid` (trace to the item) |
| `DesignArtifact` | + `distributionVersion` (int, nullable) — which approved version produced it |

- Unique `(session_id, version_number)`; unique `distribution_uuid`.
- At most one `APPROVED` per session (service-enforced + partial unique index
  if PostgreSQL-only is acceptable — open question).
- Changes via JDL + regenerate; new Liquibase changelogs included in `master.xml`.

### Enums (JDL, with es-MX values)

```text
enum DistributionStatus {
  PRELIMINARY ("Preliminar"),
  DRAFT ("Borrador"),
  PROPOSED ("Propuesta"),
  APPROVED ("Aprobada"),
  SUPERSEDED ("Reemplazada"),
  DISCARDED ("Descartada")
}

enum DistributionSource {
  MOBILE_VISIT ("Visita en obra"),
  STUDIO_EDITOR ("Editor de Studio"),
  AI_PROPOSAL ("Propuesta IA")
}

enum DistributionRow {          // used by CabinetTemplate.row (#120)
  BASE ("Bajos"),
  WALL ("Alacenas")
}

// addition to existing enum
GenerationJobType + DISTRIBUTION_PROPOSAL ("Propuesta de distribución")
```

English labels go to `i18n/en`. New values are appended to existing enums.

## Persistence rules

- Items live in `payload` (snapshot), not in rows: versions are documents;
  fabrication rows (`Cabinet`) are created only from the approved version.
- `APPROVED`, `SUPERSEDED` rows are immutable (service rejects updates).
- `PRELIMINARY` becomes read-only once a `DRAFT` has it as `parent`.

## Acceptance Criteria

- [ ] JDL + Liquibase apply on existing and empty DBs.
- [ ] Constraints: unique version per session; unique uuid.
- [ ] Payload round-trips the E13 example JSON unchanged.
- [ ] Existing E6/E8 tests pass.

## Test Plan

- Repository ITs for constraints and payload round-trip; service test for
  immutability rules.

## Open Questions

- [ ] `payload` as `text` or `jsonb`?
- [ ] Partial unique index for single `APPROVED` (PostgreSQL-specific) vs service lock.
