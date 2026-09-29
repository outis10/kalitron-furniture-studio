---
epic: E13
title: Versioned kitchen distribution (v0 on site → approved → Fusion CSV)
status: Draft
issues: "#119 #120 #121 #122 #123 #124 #125 #126 #127 #128"
gateway_epic: "outis10/kalitron-furniture-ai-gateway#39"
mobile_epic: "outis10/KFS-APP#20"
depends_on: "E12 #104 (site measurement)"
---

# E13 — Versioned kitchen distribution

Status: Draft
Epic: #118
Depends on: E12 #104 (confirmed site measurement, catalog, rules engine, roles)
Gateway epic: outis10/kalitron-furniture-ai-gateway#39 (AI proposals)
Mobile epic: outis10/KFS-APP#20 (distribution mode)

## Problem

The site measurement (E12) describes the room **as it is**. Manufacturing
needs the **distribution**: which modules go on each wall, in which order and
width. Today the cabinet plan (E6, `Cabinet` rows via `CabinetPlanService`) is
a single, unversioned snapshot, so there is no way to keep a preliminary
proposal made with the client, iterate on it, compare AI proposals, or know
which version the Fusion CSV came from.

Some first visits are only a survey; in others the client has 15–20 minutes to
sketch a first distribution (v0) with the measurer. Studio must continue from
that v0 without re-capturing anything.

## Goal

A versioned distribution per session:

- **v0** — sketched in the app during the visit, with the client, marked
  **preliminary**;
- **v1+** — refined in Studio (editor with versions and ripple), optionally
  starting from 2–3 **AI proposals**;
- exactly one **approved** version, the only source for the cabinet plan,
  Fusion CSV and Fusion scripts.

## Non-Goals

- Cut lists / BOM (E6/E11 continue from the approved cabinet plan).
- Rendering (E8/E9 consume the approved cabinet plan unchanged).
- The AI reading or deciding measurements.
- Closets and wardrobes (future phase E14 #129; this epic keeps the model generic).

## Visit types (mobile)

| Visit type | Output |
| --- | --- |
| Survey only | Site measurement (E12) |
| Survey + v0 | Site measurement + preliminary distribution v0 (KFS-APP#20) |

## Distribution model (shared by app, Studio and gateway)

A distribution is an ordered sequence of items per wall and row; **X is
derived, never stored**. This makes "changing a width shifts the following
modules" (ripple) a property of the model.

```json
{
  "distributionUuid": "uuid",
  "schemaVersion": 1,
  "basedOn": { "measurementUuid": "uuid", "measurementRevision": 4, "parentVersion": 0 },
  "libraryVersion": "2026-10-01.1",
  "catalogVersion": "2026-10-01.1",
  "walls": [
    {
      "wallCode": "A",
      "runs": [
        {
          "row": "BASE",
          "startOffsetMm": 0,
          "items": [
            { "itemUuid": "u1", "kind": "FILLER", "widthMm": 50 },
            { "itemUuid": "u2", "kind": "MODULE", "templateCode": "BASE_DRAWER_600", "widthMm": 600 },
            { "itemUuid": "u3", "kind": "MODULE", "templateCode": "BASE_SINK_800", "widthMm": 800 },
            { "itemUuid": "u4", "kind": "APPLIANCE_SLOT", "applianceType": "DISHWASHER", "widthMm": 600 },
            { "itemUuid": "u5", "kind": "GAP", "widthMm": 300 },
            { "itemUuid": "u6", "kind": "APPLIANCE_SLOT", "applianceType": "FRIDGE", "widthMm": 900 }
          ]
        },
        { "row": "WALL", "startOffsetMm": 650, "items": [ { "itemUuid": "u7", "kind": "MODULE", "templateCode": "UPPER_600", "widthMm": 600 } ] }
      ]
    }
  ],
  "acknowledgements": [ { "ruleCode": "DIST_SINK_NOT_OVER_SERVICES", "itemUuid": "u3", "reason": "Se reubica drenaje", "by": "ana", "at": "…" } ],
  "multiCook": false,
  "clientNotes": "Quiere cajones cerca de la estufa"
}
```

- Rows: `BASE` (base modules and tall modules), `WALL` (upper cabinets).
  `TALL` modules live in `BASE` and occupy the `WALL` row over their X range.
- Item kinds: `MODULE` (library template), `FILLER`, `APPLIANCE_SLOT`
  (freestanding appliance space), `GAP` (intentional free space).
- `x(item) = startOffsetMm + Σ widths of previous items in the run`.

## Version lifecycle

```text
PRELIMINARY (v0, app) ──copy──► DRAFT (v1+, Studio) ──approve──► APPROVED ──► SUPERSEDED
PROPOSED (AI) ──────────copy──► DRAFT
any non-approved ─────────────► DISCARDED
```

- One `APPROVED` version per session; approving a new one supersedes the old.
- `PRELIMINARY` is editable only from the app until Studio copies it; then it is locked.
- `APPROVED`/`SUPERSEDED` are immutable.
- Approving requires: measurement `CONFIRMED`, zero unacknowledged `ERROR`s.
- Approval materializes the cabinet plan (`Cabinet` rows) and sets
  `SessionStatus.LAYOUT_CONFIRMED` (existing value).

## Shared distribution rules

Defined once in the catalog (E12 #105) with `ruleSet: DISTRIBUTION` and scopes
`RUN` / `ITEM` / `DISTRIBUTION` (semantics in E12 #113), executed by
the Java engine (#122, extends E12 #113) and the Dart engine (KFS-APP#23),
checked by shared conformance vectors. Studio web validates through the server
(debounced validate endpoint), not a third engine.

| Code | Rule | Severity | Scope | Kind |
| --- | --- | --- | --- | --- |
| `DIST_FILLER_TOO_WIDE` | Filler wider than `filler.maxMm` | WARNING | `ITEM` | `WIDTH_ALLOWED` |
| `DIST_CLEARANCE_SINK` | Countertop landing beside sink < primary/secondary | WARNING / INFO | `ITEM` | `MIN_CLEARANCE` |
| `DIST_DISHWASHER_FAR_FROM_SINK` | Dishwasher farther than max from sink edge | WARNING | `ITEM` | `MIN_CLEARANCE` |
| `DIST_HOOD_HEIGHT` | Hood bottom below min height above cooking | WARNING | `ITEM` | `MIN_CLEARANCE` |
| `DIST_EXCEEDS_SEGMENT` | Run items don't fit between corners and blocking obstacles (door, column, full-height pipe) | ERROR | `RUN` | `FITS_SEGMENT` |
| `DIST_FILLER_REQUIRED` | Run ends at a corner, out-of-square corner or out-of-plumb wall without a filler ≥ min | WARNING | `RUN` | `FILLER_AT_END` |
| `DIST_SINK_NOT_OVER_SERVICES` | Sink module X-range doesn't contain water and drain (± tol.) | ERROR, acknowledgeable | `ITEM` | `CONTAINS_POINT` |
| `DIST_COOKING_NOT_OVER_GAS` | Range/cooktop item doesn't contain the gas point (± tol.) | ERROR, acknowledgeable | `ITEM` | `CONTAINS_POINT` |
| `DIST_CLEARANCE_FRIDGE` | Clearance beside fridge < min | WARNING | `ITEM` | `MIN_CLEARANCE` |
| `DIST_CLEARANCE_COOKING` | Landing beside range/cooktop < minimum, or cooking < min distance to tall/fridge | WARNING | `ITEM` | `MIN_CLEARANCE` |
| `DIST_CLEARANCE_COOKING_PREFERRED` | Main-side landing beside cooking < preferred | INFO | `ITEM` | `MIN_CLEARANCE` |
| `DIST_CLEARANCE_CORNER` | Doors/drawers clash at a corner (no filler/blind corner) | ERROR | `RUN` | `MIN_CLEARANCE` |
| `DIST_UPPER_COLLISION` | Upper module overlaps a window, hood zone or beam | ERROR | `ITEM` | `NO_COLLISION` |
| `DIST_UPPER_OVER_TALL` | Upper module over a tall module | ERROR | `ITEM` | `NO_COLLISION` |
| `DIST_BLOCKS_OPENING` | Base/tall item in front of a door; tall item in front of a window | ERROR | `ITEM` | `NO_COLLISION` |
| `DIST_WIDTH_NOT_ALLOWED` | Width not in the template's allowed widths | ERROR | `ITEM` | `WIDTH_ALLOWED` |
| `DIST_UNKNOWN_MODULE` | Template code not in library | ERROR | `ITEM` | `CODE_IN_LIBRARY` |
| `DIST_MEASUREMENT_NOT_CONFIRMED` | Based on an unconfirmed measurement | INFO | `DISTRIBUTION` | `STATE_IS` |

- Aisle rules (work aisle vs walkway, one cook vs `multiCook`, wall runs vs
  islands/peninsulas) and inside-room/floor-service rules: see #127 and #128.
- Clearance and tolerance values: **proposed industry-standard defaults**,
  overridable by admins without releases (#127).
- *Acknowledgeable* errors can be accepted with a reason (e.g. plumbing will be
  relocated); acknowledgements are stored on the version and shown in the
  approval summary.

## AI Gateway role (changed)

The gateway no longer reads measurements. It **proposes 2–3 distributions**
from the validated measurement, the client interview (KitchenSpec +
preferences + chat summary + v0 notes) and the module library
(outis10/kalitron-furniture-ai-gateway#39). Studio validates every proposal
with the shared rules; proposals are stored as `PROPOSED` versions with their
issues; the designer copies one into a `DRAFT`.

| Deterministic in Studio | Delegated to the gateway |
| --- | --- |
| Geometry, X derivation, validation, approval, cabinet plan, CSV/Fusion gate | Zoning ideas (where sink/cooking/fridge go), module sequence, rationale, width packing proposal |

## Issues

| Issue | Spec |
| --- | --- |
| #119 Data model | [119-distribution-data-model.md](119-distribution-data-model.md) |
| #120 Module library | [120-module-library.md](120-module-library.md) |
| #121 Distribution API | [121-distribution-api.md](121-distribution-api.md) |
| #122 Shared distribution rules | [122-distribution-rules.md](122-distribution-rules.md) |
| #123 Studio editor | [123-distribution-editor.md](123-distribution-editor.md) |
| #124 Approval + CSV/Fusion gate | [124-approve-and-fusion-gate.md](124-approve-and-fusion-gate.md) |
| #125 AI proposals (Studio side) | [125-ai-distribution-proposals.md](125-ai-distribution-proposals.md) |
| #126 `ROLE_DESIGNER` | [126-designer-role.md](126-designer-role.md) |
| #127 Configurable rule parameters | [127-configurable-rule-params.md](127-configurable-rule-params.md) |
| #128 Islands and peninsulas | [128-islands-and-peninsulas.md](128-islands-and-peninsulas.md) |

Gateway: outis10/kalitron-furniture-ai-gateway#40 (contract), #41 (endpoint),
#42 (evaluation). Mobile: outis10/KFS-APP#21 (library), #22 (distribution
mode), #23 (Dart rules), #24 (sync v0).

## Decisions (2026-09-29)

| Decision | Issue |
| --- | --- |
| Standard default clearances/tolerances, admin-configurable | #127 |
| E6 `PUT/POST /cabinet-plan` blocked once a distribution exists | #124 |
| New `ROLE_DESIGNER` (+ `assignedDesigner`) | #126 |
| Islands and peninsulas included in this epic | #128 |
| Generic model (`LayoutDistribution` + `projectType`) to adapt to closets later | E14 #129 |

## Relationship with existing epics

- **E6** (`CabinetPlanService`, `CabinetPlanValidator`, `PUT/POST /cabinet-plan`):
  the cabinet plan becomes the **projection of the approved distribution**;
  direct writes through the E6 endpoints are **blocked** once a session has a
  distribution (#124).
- **E8/E9** (prototype, styled render) and **E11** (#99–#103 Fusion script):
  keep reading the cabinet plan; they must run only when an approved
  distribution exists (#124).
- **E12**: the measurement is the input; `MEASURED` precedes `LAYOUT_CONFIRMED`.

## Suggested order

1. #126 role, #127 params, #120 library + #122 rules/vectors (contracts) — after E12 #105/#113.
2. #119 model → #121 API.
2b. #128 islands/peninsulas once the wall-run flow works end to end.
3. KFS-APP#21 → #22 → #23 → #24 (v0 on site).
4. #123 Studio editor → #124 approval + gate.
5. outis10/kalitron-furniture-ai-gateway#40 → #41 → #42, then #125.

## Risks

| Risk | Mitigation |
| --- | --- |
| Two ways to edit the cabinet plan (E6 vs distribution) | E6 writes blocked when a distribution exists (#124). |
| Rule parameters (clearances) not validated with Kalitron | Standard defaults, admin-overridable (#127); mostly WARNING until confirmed. |
| Islands need full room geometry | Derived from `RoomWall` positions; rules skipped with INFO when the room is not closed (#128). |
| AI proposals invalid or generic | Studio validates all; repair retry with issues; evaluation set (gw#42); designer always decides. |
| v0 made on unconfirmed measurement | Allowed with INFO; Studio copies v0 onto the confirmed measurement; rules re-run. |
| Library changes break old versions | Versions store `libraryVersion`; unknown templates flagged, never silently changed. |

## Open questions

- [ ] Kalitron review of the proposed default parameters (#127).
- [ ] Should the client see/approve v0 or v1 (e.g. through the public proposal page)?
- [ ] Merge `CabinetPlanValidator` rules into the declarative engine, or keep both during transition?
