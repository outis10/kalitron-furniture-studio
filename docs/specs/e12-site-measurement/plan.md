# E12: On-Site Laser Measurement Capture (v2 plan)

Status: Draft
Repos: `outis10/kalitron-furniture-studio`, `outis10/KFS-APP` (Flutter, new),
`outis10/kalitron-furniture-ai-gateway` (no E12 work)
Epics: Studio #104 · Mobile outis10/KFS-APP#1–#4
Supersedes: [superseded/plan-v1-formal-croquis.md](superseded/plan-v1-formal-croquis.md)

## Why the plan changed

v1 of this plan read formal paper croquis (QR + ArUco sheets) with an LLM
through a gateway v2 endpoint. Decisions taken since:

1. On-site measurement is captured with a **new Flutter app** (`KFS-APP`,
   Android + iOS).
2. Measurements come from a **Bosch GLM 50-27 C** laser over Bluetooth LE
   (Bosch MT protocol; the device does not advertise its name, it is
   discovered by Service UUID and its identifier is decoded; Bosch offers an
   SDK and sample apps through its developer community). **Manual entry is
   always available** as a fallback.
3. Paper is no longer the main flow. What remains:
   - gateway `POST /api/v1/sketch/analyze` (informal sketches, external
     carpenters) — **unchanged**;
   - a printable floor-plan sheet as a **backup** when the device fails.
4. Measurements arrive as structured data; **no LLM reads numbers** in the
   formal flow.

## Impact on the v1 issues

| Plan id | Issue | Decision | Justification |
| --- | --- | --- | --- |
| G1 | gw#32 | **Delete** | No gateway v2 endpoint; the measurement contract now lives in Studio (#112). |
| G2 | gw#33 | **Delete** | No photographed sheets to rectify; ArUco/QR not needed. |
| G3 | gw#34 | **Delete** | App sends structured data straight to Studio. |
| G4 | gw#35 | **Delete** | The LLM no longer reads numbers in the formal flow. |
| G5 | gw#36 | **Delete** | Extended schema moves to the Studio measurement API (#112) and data model (#107). |
| G6 | gw#37 | **Delete (moved)** | Validator moves to declarative rules in Studio (#113) executed by Studio and the app (KFS-APP#13). |
| G7 | gw#38 | **Delete** | No LLM extraction to measure; replaced by validation conformance vectors (#113). |
| S1 | #105 | **Modify** | Catalog stays the single source of truth; now also carries declarative validation rules and is consumed offline by the app. |
| S2 | #106 | **Modify** | Reduced to one printable backup floor-plan sheet; no QR/ArUco, no elevation sheets, no reserve sheets. |
| S3 | #107 | **Modify** | Model a versioned `SiteMeasurement` aggregate with per-value source (laser/manual) and wall photos; drop sheet/reserve fields. |
| S4 | #108 | **Delete** | Web multi-sheet upload replaced by the mobile app + sync API (#112). |
| S5 | #109 | **Delete** | Review/confirm happens in the app (KFS-APP#15); Studio re-validates authoritatively on sync. |
| S6 | #110 | **Modify** | Conversion source is the confirmed `SiteMeasurement`, not a gateway extraction; still sets `MEASURED`. |
| S7 | #111 | **Delete** | No reserve sheets. |

New issues: Studio #112 (sync API), #113 (rules engine + vectors), #114
(mobile app config + APK distribution), #115 (mobile refresh tokens), #116
(`ROLE_MEASURER` + assignment); mobile epics KFS-APP#1–#4 with #5–#18.

## Problem

Measuring a kitchen/closet on site is done by hand and transcribed later.
Transcription errors and missing data are discovered at the office, forcing a
second visit. The informal-sketch extraction (E7) is useful for external
carpenters but is not precise enough for Kalitron's own designers.

## Goal

A designer measures a room on site with a phone and a Bosch GLM 50-27 C,
gets instant validation without connectivity, attaches a photo per wall,
confirms the measurement, and the app syncs it to Studio, which re-validates
it authoritatively, converts it to a measured layout and sets the session to
`MEASURED`.

## Non-Goals

- Changing gateway v1 (`/api/v1/sketch/analyze`) or E7.
- Reading paper croquis with AI.
- Rendering or quoting from the app. (A simple preliminary distribution v0
  **is** in scope, but in E13 — see *Distribution layer* below.)
- Supporting laser models other than GLM 50-27 C in this epic (design the
  BLE layer so more can be added later).
- Web review of the measurement in Studio (read-only view can come later).

## Distribution layer (E13)

The measurement describes the room **as it is**; the **distribution** (which
modules go on each wall) is what produces the Fusion CSV. It is planned as a
separate epic, **E13 #118**, on top of this one:

- Visit types in the app: *survey only* or *survey + v0 distribution* (15–20
  min with the client, marked preliminary) — KFS-APP#20.
- Versioned `LayoutDistribution`: v0 (app) → v1+ (Studio editor with ripple)
  → one approved version; Studio continues from v0 without re-capture.
- Distribution rules share this epic's declarative engine and conformance
  vectors (E13 #122 extends #113).
- The AI Gateway proposes 2–3 distributions from the validated measurement,
  interview and module library (gateway epic outis10/kalitron-furniture-ai-gateway#39).
- Cabinet plan, Fusion CSV and scripts come only from the approved version (E13 #124).

See [../e13-kitchen-distribution/epic.md](../e13-kitchen-distribution/epic.md).

## Capture Rules (kept from v1)

| Rule | Value |
| --- | --- |
| Units | Millimeters only |
| Wall codes | `A`, `B`, `C`… clockwise, starting left of the entry door |
| Corners | `E-AB` between wall A and B; angle only if ≠ 90° |
| Wall length | Three heights: floor, 900 mm, ceiling; design uses the minimum |
| X | Cumulative from the wall's left corner, facing the wall |
| Y | Height from finished floor |
| Element fields | `X`, `Y`, `A` (width), `H` (height), depth where required |
| Evidence | At least one photo per wall |

### Nomenclature (single source of truth — Studio catalog #105)

| Group | Codes (es-MX label) | Required data |
| --- | --- | --- |
| Walls/corners | `A`…`n` Muro, `E-AB` Esquina entre muros A y B | 3 lengths; angle if ≠ 90° |
| Openings | `V` Ventana, `P` Puerta | X, A, H, sill Y; swing for `P` |
| Services | `TA` Toma de agua, `DR` Drenaje, `GS` Toma de gas, `CT` Contacto eléctrico, `AP` Apagador, `CE` Salida de extracción | X, Y |
| Obstructions | `CL` Columna, `VG` Viga, `TB` Tubería, `RG` Registro | X, A, H or depth |
| Appliances | `RF` Refrigerador, `ES` Estufa, `PA` Parrilla, `HO` Horno, `CA` Campana, `MW` Microondas, `LV` Lavavajillas, `TJ` Tarja (espacio) | A, H, depth |
| Site | `dP` Desnivel de piso, `dPl` Desplome de muro | value in mm + location |

Full catalog with enum mappings: [105-nomenclature-catalog.md](105-nomenclature-catalog.md).

### Validation rules (defined once in the catalog, #113)

| Code | Rule | Severity | Scope | Change vs v1 |
| --- | --- | --- | --- | --- |
| `WALL_CLOSURE_MISMATCH` | Rightmost element X + A + closing measurement ≠ wall design length (tol. 5 mm) | ERROR | `WALL` | Replaces `WALL_SUM_MISMATCH` (see *Free spans* below) |
| `WALL_CLOSURE_MISSING` | Wall has elements with width but no closing measurement | WARNING | `WALL` | New |
| `ELEMENTS_OVERLAP` | Two elements with width and height overlap in the wall plane (services excluded) | WARNING | `ELEMENT` | New |
| `WALL_LENGTH_SPREAD` | max − min of 3 lengths > 5 mm | WARNING | `WALL` | — |
| `FLOOR_OUT_OF_LEVEL` | `dP` > 5 mm | WARNING | `SITE` | — |
| `CORNER_NOT_SQUARE` | Angle ≠ 90° | WARNING | `SITE` | — |
| `ELEMENT_MISSING_XY` | Element without X or Y | ERROR | `ELEMENT` | — |
| `APPLIANCE_MISSING_DIMS` | Appliance without A, H, depth | ERROR | `ELEMENT` | — |
| `ELEMENT_OUT_OF_WALL` | X + A > wall design length | ERROR | `ELEMENT` | — |
| `UNKNOWN_CODE` | Code not in catalog | ERROR | `ELEMENT` | Server-side guard only; app uses buttons |
| `MANUAL_VALUE` | Value entered manually, not from laser | INFO | `WALL` | Replaces `LOW_CONFIDENCE` |
| `WALL_INCOMPLETE` | Wall in floor plan without 3 lengths | ERROR | `WALL` | Replaces `WALL_WITHOUT_SHEET` |
| `WALL_WITHOUT_PHOTO` | Wall without evidence photo | WARNING | `WALL` | New |

### Free spans ("huecos") — decision

**Free spans are not captured.** Because every X is measured from the wall's
left corner (with the laser, each X is an independent reading, so errors do
not accumulate), gaps between elements are derived, not measured. The app and
Studio compute them for display only.

What v1's `WALL_SUM_MISMATCH` really protected against — a wrong wall length or
a misread position — is covered by a **closing measurement** (`closingMm`): an
optional laser reading from the wall's **right** corner to the right edge of
the rightmost element. Then `X + A + closingMm ≈ designLength` (± 5 mm) is an
independent cross-check (`WALL_CLOSURE_MISMATCH`). If it is not taken, the app
warns (`WALL_CLOSURE_MISSING`) but does not block. `ELEMENTS_OVERLAP` catches
two elements typed at the same position.

## Roles and authentication — decision

- New `ROLE_MEASURER` (least privilege) + `DesignSession.assignedMeasurer`
  (#116). A measurer only sees and syncs assigned sessions; `ROLE_ADMIN`
  can do everything.
- Mobile uses **refresh tokens** (#115): 1 h access JWT + rotating, per-device,
  revocable refresh token (60 days sliding). Web login unchanged.

## Distribution — decision (pilot)

- Android only, **internal direct APK** hosted by Studio (#114); the app
  downloads, verifies sha256 and launches the Android installer (KFS-APP#7).
- iOS distribution deferred; architecture stays cross-platform.

## Where validations live

**Principle:** rules are *defined* once (declarative, in the catalog) and
*executed* twice — in the app (instant, offline) and in Studio
(authoritative on sync).

1. **Declarative definition** — the catalog JSON (#105) contains
   `validationRules[]`: `code`, `kind`, `severity`, `params` (e.g.
   `toleranceMm: 5`), `ruleSet` (`MEASUREMENT` here; `DISTRIBUTION` in E13),
   `scope` (`WALL`, `ELEMENT`, `SITE`, `MEASUREMENT`), `prerequisites`,
   `messageEsMx` template. Tolerances and severities are data, not code.
2. **Small closed set of rule kinds** — each `kind` (e.g. `SUM_WITHIN_TOLERANCE`,
   `SPREAD_WITHIN_TOLERANCE`, `REQUIRED_FIELDS`, `WITHIN_BOUNDS`,
   `THRESHOLD_EXCEEDED`, `CODE_IN_CATALOG`, `SOURCE_IS`, `HAS_ATTACHMENT`,
   `NO_OVERLAP`) is
   implemented once in Java (Studio) and once in Dart (app). New rules that
   reuse an existing kind need no code change in either side.
3. **Shared conformance vectors** — Studio owns
   `validation-vectors/*.json` (input measurement + catalog version →
   expected issues). Studio CI runs them against the Java engine; the app CI
   downloads the same versioned vectors and runs them against the Dart engine.
   A rule change is not mergeable until both pass.
4. **Versioning** — catalog has `catalogVersion`, and each rule kind set has a
   `rulesEngineVersion`. A catalog declares `minAppVersion` when it introduces a
   new `kind`; older apps show "update required" (see KFS-APP#7 / #114) instead
   of silently skipping rules.
5. **Authority** — every sync payload carries the `catalogVersion` used on
   device. Studio always re-validates with its current catalog and returns
   the authoritative `validationIssues[]`; confirmation is decided by Studio.
   If the app's catalog is stale, the response tells it to refresh.

## Studio issues

### #105 (S1, modified) — Nomenclature catalog and validation rules export

- Static catalog JSON with codes, groups, es-MX labels, required fields,
  mapping to `RoomObstacleType` / `ApplianceType`, and `validationRules[]`.
- `GET /api/croquis/catalog` (authenticated) with `catalogVersion`,
  `minAppVersion`, ETag.
- Acceptance:
  - [ ] Every code and rule in the tables above is present.
  - [ ] Enum parity tests with `RoomObstacleType` / `ApplianceType`.
  - [ ] `If-None-Match` returns `304` when unchanged.

### #106 (S2, modified) — Printable backup floor-plan sheet

- One-page PDF per session: grid, wall/corner boxes, three-length boxes,
  element table with nomenclature legend, session code.
- No QR, no ArUco, no LLM; data is typed into the app later (manual source).
- Acceptance:
  - [ ] Prints legibly on letter paper at 100%.
  - [ ] Legend generated from the catalog (#105).

### #107 (S3, modified) — Data model for site measurements

- New aggregate `SiteMeasurement` (client UUID, session, revision, status
  `DRAFT/CONFIRMED`, `catalogVersion`, device info, measuredBy, timestamps,
  payload JSON snapshot).
- `RoomWall`: `wallCode`, `lengthFloorMm`, `length900Mm`, `lengthCeilingMm`,
  `outOfPlumbMm`; `lengthMm` = minimum.
- `RoomObstacleType`: `SWITCH`, `EXHAUST`, `BEAM`, `PIPE`, `ACCESS_PANEL`;
  `RoomObstacle.applianceType`, `croquisCode`.
- `KitchenSpec`: `floorOutOfLevelMm`, note.
- `DesignImage`: `wallCode` + new `ImageType.SITE_PHOTO`, link to `SiteMeasurement`.
- `SessionStatus.MEASURED`.
- Acceptance:
  - [ ] JDL + new Liquibase changesets apply on existing and empty DBs.
  - [ ] E6/E7 tests pass unchanged.

### #110 (S6, modified) — Convert confirmed measurement to measured layout

- On confirm, map `SiteMeasurement` → `RoomWall` / `RoomObstacle` /
  `KitchenSpec` reusing the E7 #62 conversion; set `MEASURED`.
- Acceptance:
  - [ ] One transaction; idempotent per measurement revision.
  - [ ] Any `ERROR` after server re-validation blocks confirmation.

### #112 (new) — Site measurement sync API (idempotent, versioned) and photos

- `PUT /api/design-sessions/{sessionId}/site-measurements/{measurementId}`
  with `schemaVersion`, `revision`, `baseRevision`, `catalogVersion`.
- `PUT …/{measurementId}/photos/{photoId}` (multipart, client UUID, sha256).
- `POST …/{measurementId}/confirm`.
- Acceptance:
  - [ ] Replaying the same request returns the same result, no duplicates.
  - [ ] Stale `baseRevision` returns `409` with the server revision.
  - [ ] Response always includes authoritative `validationIssues[]`.
  - [ ] Unsupported `schemaVersion` returns `400` with supported versions.

### #113 (new) — Declarative validation rules engine and conformance vectors

- Java engine executing catalog `validationRules[]` by `kind`.
- Versioned conformance vectors published for the app CI.
- Acceptance:
  - [ ] All rules in the table covered by vectors (pass and fail, tolerance edges).
  - [ ] Changing a tolerance in the catalog changes results without code changes.

### #114 (new) — Mobile app config and internal APK distribution

- `GET /api/mobile/app-config` (public): `minSupportedVersion`,
  `latestVersion(Code)`, `distribution` (`DIRECT_APK` for the pilot),
  `downloadUrl`, `sha256`, release notes, `catalogVersion`.
- `GET /api/mobile/releases/android/{versionCode}/kfs-app.apk` with `Range` support.
- Acceptance:
  - [ ] Values configurable without redeploying the app.
  - [ ] Served APK matches the advertised `sha256`.
  - [ ] Response contains no user or client data.

### #115 (new) — Mobile refresh tokens

- `POST /api/mobile/auth/login|refresh|logout`; 1 h access JWT + opaque
  rotating refresh token per device (60 days sliding, 180 absolute), stored
  hashed; reuse detection; admin device revocation. Web login unchanged.
- Acceptance:
  - [ ] Rotation, reuse detection and revocation covered by tests.
  - [ ] Only measurer/admin users can obtain mobile tokens.

### #116 (new) — `ROLE_MEASURER` and session assignment

- New authority `ROLE_MEASURER`; `DesignSession.assignedMeasurer`;
  `GET /api/mobile/sessions` (assigned sessions, minimal DTO);
  admin assigns from session detail.
- Acceptance:
  - [ ] A measurer only sees/syncs assigned sessions (`403` otherwise).
  - [ ] Mobile DTO has no email, phone, chat or quote data.

## Mobile issues (KFS-APP)

### Epic M1 (KFS-APP#1) — App foundation

- **#5 Project setup, architecture and CI** — Flutter stable, feature-first
  layered architecture, flavors (dev/staging/prod), lints, CI (analyze, test,
  signed release APK).
  - [ ] `flutter analyze` clean, tests run in CI, signed APK produced.
- **#6 Authentication against Studio** — mobile login + refresh tokens (#115),
  secure storage, silent refresh, role check (#116), offline behavior.
  - [ ] Login once per device; silent refresh on reconnect; revoked device forced to log in.
- **#7 Auto-update** — check #114 at start; forced below `minSupportedVersion`,
  optional below `latestVersion`; pilot: download APK, verify sha256, launch
  Android installer.
  - [ ] v1 APK updates itself to v2 on a real Android device.

### Epic M2 (KFS-APP#2) — Bosch GLM 50-27 C over BLE

The physical device is **not available yet**. Laser work is split so nothing
else waits for it; the app is **manual-first** and laser is an input mode.

- **#8 Spike (no device)** — research Bosch SDK access and community
  libraries (license, platforms, protocol coverage for GLM 50-27 C), define the
  `LaserDevice` interface and build a **simulated laser** (fake device emitting
  readings) used in development and tests.
  - [ ] Written comparison of integration paths with license notes and a recommendation.
  - [ ] Simulated laser drives the capture UI end to end.
- **#9 BLE laser service** — adapter implementing the interface using the
  chosen path; discovery by Service UUID, identifier decoding, reconnect,
  stream in mm; manual/automatic mode toggle.
  - [ ] Works against the simulator and recorded frames; disconnection never blocks manual entry.
- **#18 Hardware validation (requires device)** — validate #9 on a real
  GLM 50-27 C on Android (and iOS when available).
  - [ ] 10 consecutive readings match the device display on Android.
  - [ ] Value reaches the focused field ≤ 1 s after pressing the device button.

### Epic M3 (KFS-APP#3) — On-site measurement capture

- **#10 Download assigned sessions (#116) and catalog for offline use**.
  - [ ] Assigned sessions and catalog available with airplane mode on.
- **#11 Floor plan capture (walls and corners)**.
  - [ ] Walls created clockwise `A…n`, corners `E-XY` with angle if ≠ 90°.
- **#12 Wall capture with nomenclature buttons** — 3 lengths, optional
  closing measurement, elements via catalog buttons, manual or automatic
  (laser) mode, source recorded.
  - [ ] Every catalog code has a button; required fields come from the catalog.
- **#13 Instant on-device validation** — Dart engine for catalog rules;
  passes Studio conformance vectors.
  - [ ] Issues update on every edit, offline, < 100 ms per wall.
- **#14 Wall evidence photos**.
  - [ ] ≥ 1 photo per wall stored locally, compressed, synced later.
- **#15 Review and confirm** — per-wall summary, issues list, confirm blocked
  while any `ERROR` exists; final state decided by Studio.
  - [ ] Confirm offline queues the request; UI shows "pending server confirmation".

### Epic M4 (KFS-APP#4) — Offline-first storage and sync

- **#16 Local storage** — SQLite (drift), migrations, encrypted at rest where
  supported.
  - [ ] App kill/restart never loses captured data.
- **#17 Sync engine** — outbox, idempotent `PUT`s with client UUIDs,
  revisions, retries with backoff, photo upload resume, conflict UI.
  - [ ] Replayed sync creates no duplicates in Studio.
  - [ ] `409` shows a conflict screen; nothing is overwritten silently.

## Risks and mitigations

| Risk | Impact | Mitigation |
| --- | --- | --- |
| No physical GLM 50-27 C yet | Laser integration unverified | Manual-first product; simulated laser behind `LaserDevice` interface (#8); hardware validation isolated in KFS-APP#18; buy/borrow a device early. |
| Bosch SDK access/licensing; community libraries with unclear licenses | Legal/maintenance risk | Compare official SDK vs community libraries in #8 (license, maintenance, platforms); prefer official SDK or permissive licenses; wrap any choice behind the adapter so it can be swapped. |
| MT protocol changes / firmware differences | Wrong readings | Pin tested firmware; unit tests on recorded frames; show raw value + unit; reject non-mm/unknown frames. |
| BLE permissions (Android 12+: `BLUETOOTH_SCAN`/`BLUETOOTH_CONNECT`; ≤ 11: location + location services; iOS: `NSBluetoothAlwaysUsageDescription`) | Device not found | Permission primer screens, graceful denial → manual mode, test matrix per OS version. |
| Device not advertising name | Discovery failure | Scan by Service UUID; decode identifier; let user pick among multiple devices. |
| Direct APK distribution | Users must allow "install unknown apps"; lost keystore blocks updates | One-time onboarding guide; sha256 check; release keystore backed up securely outside the repo; single signing identity forever. |
| Offline sync conflicts (two devices, re-edits) | Data loss | Client UUID + revision + `baseRevision`; `409`, never silent last-write-wins; one assigned measurer per session. |
| Sites without signal | Cannot sync | Offline-first; prefetch before visit; capture never requires network; sync on reconnect. |
| Lost/stolen phone | Data exposure | Refresh tokens revocable per device (#115); secure storage; minimal client data on device. |
| Validation drift between Dart and Java | Different results | Declarative rules + conformance vectors in both CIs + `minAppVersion` gate. |
| Photo size on mobile data | Slow sync | Compress on device, resumable upload, Wi-Fi-only option. |

## Suggested implementation order

The device is not available, so the laser is **not** on the critical path.

1. KFS-APP#8 spike without device: SDK/community library research +
   `LaserDevice` interface + simulator (in parallel with step 2).
2. Studio contracts: #105 catalog + rules, #113 engine/vectors, #116 role,
   #115 refresh tokens.
3. #107 data model → #112 sync API → #114 app config/APK hosting.
4. KFS-APP#5 setup → KFS-APP#6 auth → KFS-APP#7 auto-update (APK).
5. KFS-APP#16 local storage → KFS-APP#10 download → KFS-APP#11 floor plan →
   KFS-APP#12 wall capture (**manual mode**, simulator for automatic) →
   KFS-APP#13 validation → KFS-APP#14 photos.
6. KFS-APP#17 sync → KFS-APP#15 review/confirm → Studio #110 conversion.
   **Pilot with manual mode is possible here.**
7. KFS-APP#9 BLE adapter → KFS-APP#18 hardware validation as soon as a device arrives.
8. #106 backup sheet (any time).

## Decisions log

| Date | Decision |
| --- | --- |
| 2026-09-29 | Laser capture via Flutter app; LLM no longer reads numbers in formal flow. |
| 2026-09-29 | Manual and automatic (laser) modes; manual-first while no device is available. |
| 2026-09-29 | Pilot distribution: internal direct APK (Android); iOS deferred. |
| 2026-09-29 | Mobile refresh tokens (#115). |
| 2026-09-29 | New `ROLE_MEASURER` + `DesignSession.assignedMeasurer` (#116). |
| 2026-09-29 | Free spans not captured; closing measurement + overlap rules replace `WALL_SUM_MISMATCH`. |
| 2026-09-29 | Distribution layer added as E13 (#118): versioned v0→approved, shared rules, AI proposals, CSV only from approved. |

## Open questions

- [ ] Which laser integration path (official SDK vs community library) — answered by KFS-APP#8.
- [ ] Final distribution after the pilot (Play Store / private track / MDM); iOS channel.
- [ ] Use Shorebird (code push) for Dart-only hotfixes?
- [ ] Google sign-in (Studio has `/api/auth/google`) on mobile?
- [ ] Public vs authenticated APK download (#114).
- [ ] Where does `MEASURED` sit relative to `CHATTING`?
- [ ] Floor level on `KitchenSpec` or a separate site record?
- [ ] Re-measure — new measurement or new revision?
- [ ] Photo retention and privacy policy for client homes.
