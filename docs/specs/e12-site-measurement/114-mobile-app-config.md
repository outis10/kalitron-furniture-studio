# [E12] Issue 114: Mobile App Config and Internal APK Distribution

Status: Draft
Issue: #114
Epic: #104
Related: outis10/KFS-APP#7 (auto-update), #105 (`minAppVersion` in catalog)
Owner: TBD

## Problem

The app needs to know, before login and without a new release, whether it is
too old (forced update), whether a newer version exists (optional update) and
which catalog version is current. Initial distribution is **internal, via
direct APK** (no Play Store), so Studio must also host the APK and its checksum.

## Goal

A public app-config endpoint with remotely configurable version policy, plus
hosting of signed Android APK releases for in-app self-update.

## Non-Goals

- iOS distribution (deferred; see open questions).
- Public store listings.

## API Contract

`GET /api/mobile/app-config?platform=android|ios` (public)

```json
{
  "platform": "android",
  "minSupportedVersion": "1.0.0",
  "latestVersion": "1.2.0",
  "latestVersionCode": 12,
  "distribution": "DIRECT_APK",
  "downloadUrl": "https://studio.example/api/mobile/releases/android/12/kfs-app.apk",
  "sha256": "9f2c…",
  "fileSizeBytes": 38123456,
  "releaseNotesEsMx": "Mejoras en la conexión del láser.",
  "catalogVersion": "2026-10-01.1",
  "maintenance": false
}
```

- `distribution`: `DIRECT_APK` now; `PLAY_STORE`, `APP_STORE`, `TESTFLIGHT` later
  (then `downloadUrl` becomes the store URL and `sha256` is null).
- `400` invalid platform. `Cache-Control: max-age=300`.

`GET /api/mobile/releases/android/{versionCode}/kfs-app.apk` (public, see open question)
- Streams the APK with `Content-Type: application/vnd.android.package-archive`,
  `Content-Length`, supports `Range` (resumable download).
- `404` unknown version.

## Release management

- APKs stored under `${OUTPUT_DIR}/mobile-releases/android/{versionCode}/`
  (or object storage — open question).
- Release metadata (`versionName`, `versionCode`, `sha256`, size, notes,
  `minSupportedVersion`) configured via `application.yml` `app.mobile.*` /
  env vars for the pilot; an admin upload screen can come later.
- APKs are signed with the **same release keystore** every time (Android
  refuses updates with a different signature). Keystore kept outside the repo.

## Security

- The APK is not secret (login is required to do anything); making it public
  allows forced updates before login.
- The app verifies `sha256` before launching the installer; Android verifies
  the signature matches the installed app.
- HTTPS only in staging/prod.

## Backend Behavior

- Resource: `web/rest/custom/MobileAppConfigResource`, `MobileReleaseResource`.
- `catalogVersion` from `CroquisCatalogService`.
- Security config: `permitAll` for `GET /api/mobile/app-config` and
  `GET /api/mobile/releases/**`.
- Response contains no user or client data.

## Acceptance Criteria

- [ ] App-config reachable without JWT; values change via env vars without an app release.
- [ ] APK download supports `Range` and returns correct `Content-Length`.
- [ ] `sha256` in app-config matches the served file (test).
- [ ] Response contains no user or client data.

## Test Plan

- Backend: resource ITs (public 200, 400, 404, range request), checksum test.
- Manual: install v1 APK, publish v2, app self-updates.

## Open Questions

- [ ] Public APK download, or require login (then forced update only after login)?
- [ ] Storage for APKs: local disk vs object storage.
- [ ] iOS internal distribution when needed: TestFlight (needs Apple Developer
      account) vs Ad Hoc (100 devices) vs Apple Business Manager custom app.
