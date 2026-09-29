---
epic: E12
title: On-site laser measurement capture (mobile app + Studio)
status: Draft
issues: "#105 #106 #107 #110 #112 #113 #114 #115 #116"
mobile_epics: "outis10/KFS-APP#1 #2 #3 #4"
decisions: "manual-first (no device yet), direct APK pilot, refresh tokens, ROLE_MEASURER, closing measurement instead of free spans"
---

# E12 — On-site laser measurement capture

Status: Draft
Epic: #104
Mobile epics: outis10/KFS-APP#1 (foundation), #2 (BLE laser), #3 (capture), #4 (offline/sync)
Plan: [plan.md](plan.md) · superseded v1: [superseded/plan-v1-formal-croquis.md](superseded/plan-v1-formal-croquis.md)

## Goal

Designers measure on site with the KFS-APP Flutter app and a Bosch GLM 50-27 C
(BLE, manual fallback). The app validates instantly offline and syncs a
structured, versioned measurement to Studio, which re-validates it, converts
it to a measured layout and sets the session to `MEASURED`.

## Flow

```text
Studio session ─► app downloads sessions + catalog (#105, KFS-APP#10)
  ─► floor plan + per-wall capture (laser/manual) + photos (KFS-APP#11–#14)
  ─► instant validation from catalog rules (KFS-APP#13)
  ─► confirm (KFS-APP#15) ─► outbox sync (KFS-APP#17)
  ─► Studio sync API (#112) ─► authoritative validation (#113)
  ─► conversion to measured layout + MEASURED (#110)
```

## Studio issues

| Plan | Issue | Decision | Spec |
| --- | --- | --- | --- |
| S1 | #105 | Modify | [105-nomenclature-catalog.md](105-nomenclature-catalog.md) |
| S2 | #106 | Modify | [106-backup-floor-plan-sheet.md](106-backup-floor-plan-sheet.md) |
| S3 | #107 | Modify | [107-data-model-site-measurement.md](107-data-model-site-measurement.md) |
| S4 | #108 | Deleted | — (replaced by KFS-APP + #112) |
| S5 | #109 | Deleted | — (review in KFS-APP#15) |
| S6 | #110 | Modify | [110-convert-site-measurement-to-measured-layout.md](110-convert-site-measurement-to-measured-layout.md) |
| S7 | #111 | Deleted | — (no reserve sheets) |
| new | #112 | New | [112-site-measurement-sync-api.md](112-site-measurement-sync-api.md) |
| new | #113 | New | [113-validation-rules-engine.md](113-validation-rules-engine.md) |
| new | #114 | New | [114-mobile-app-config.md](114-mobile-app-config.md) |
| new | #115 | New | [115-mobile-refresh-tokens.md](115-mobile-refresh-tokens.md) |
| new | #116 | New | [116-measurer-role-and-assignment.md](116-measurer-role-and-assignment.md) |

Gateway E12 issues (gw#31–#38) are closed as not planned; gateway v1 is unchanged.

## Epic acceptance criteria

- [ ] A designer captures a full room offline with laser or manual values and
      photos per wall.
- [ ] App and Studio produce identical validation results for the shared
      conformance vectors.
- [ ] Sync is idempotent; conflicts are surfaced, never overwritten silently.
- [ ] Confirming creates walls/obstacles with extended fields and sets the
      session to `MEASURED`.
- [ ] Gateway v1 sketch flow keeps working unchanged.
