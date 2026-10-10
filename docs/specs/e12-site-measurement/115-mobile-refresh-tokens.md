# [E12] Issue 115: Mobile Refresh Tokens (Rotating, per Device, Revocable)

Status: Reviewed
Issue: #115
Epic: #104
Related: outis10/KFS-APP#6 (mobile auth), #116 (measurer role, implemented), #142 (security hardening)
Owner: TBD

## Problem

Studio issues stateless JWTs (24 h, or 30 days with `rememberMe`) and has no
refresh or revocation. For a field app that is a bad trade-off:

- a 30-day bearer token on a phone cannot be revoked if the phone is lost;
- a short token forces measurers to type passwords on site, often offline.

## Current state (verified 2026-10-10)

- `AuthenticateController.createToken(Authentication, rememberMe)` builds the
  JWT inline (claims `sub`, `auth`, `userId`; HS512 via `JwtEncoder`); validity
  from `jhipster.security.authentication.jwt.token-validity-in-seconds*`.
- `SecurityConfiguration` (after #116): `/api/mobile/**` requires
  `ROLE_ADMIN` or `ROLE_MEASURER`; `/api/admin/**` requires `ROLE_ADMIN`.
- `@EnableScheduling` is on (`AsyncConfiguration`); no rate-limiting library.
- JPA repositories are scanned in `com.kalitron.studio.repository`; entities
  anywhere under `com.kalitron.studio`.

## Decision

**Refresh tokens for mobile only.** Short-lived access JWT + long-lived,
rotating, revocable refresh token bound to a device. The web login stays
exactly as it is.

## Goal

Measurers log in once per device and stay logged in while they keep using the
app; admins can revoke a lost device and it stops working within one access-token lifetime.

## Non-Goals

- Changing the web (`/api/authenticate`) flow or its tokens.
- OAuth2/OIDC server migration.
- Admin web UI for devices (the pilot uses the API / Swagger; UI later).
- Rate limiting (deferred to pre-MVP hardening, #142).

## Token policy

| Token | Format | Lifetime | Storage |
| --- | --- | --- | --- |
| Access | Existing JHipster JWT (same claims) | 1 h — `app.mobile.access-token-seconds` | App memory + secure storage |
| Refresh | Opaque random 256-bit, base64url (43 chars) | 60 days sliding (`app.mobile.refresh-token-days`), 180 days absolute (`app.mobile.refresh-token-absolute-days`) | Device: Keystore/Keychain. Server: **SHA-256 hash only** |

- **Family:** one login on one device starts a family (`familyId`). Each refresh
  rotates: the presented token is marked replaced and a new token of the same
  family is returned. Sliding expiry = `min(now + 60 d, familyAbsoluteExpiresAt)`.
- **One active family per (user, deviceId):** a new login on the same device
  revokes the previous family.
- **Reuse detection:** presenting a token that was already rotated revokes the
  whole family → `401`.
- **Grace window (flaky networks):** if the already-rotated token is presented
  again within `app.mobile.refresh-reuse-grace-seconds` (default 30) of its
  rotation, from the same `deviceId`, it is treated as a retry: the successor
  issued at rotation is revoked and a fresh successor is returned; the family
  survives. After the window → reuse detection.
- **Revocation latency:** revoking (logout or admin) stops refreshes
  immediately; an access JWT already issued stays valid until it expires (≤ 1 h).
  Accepted for the pilot; lower `access-token-seconds` if needed.
- **Re-check on every refresh:** the user must still exist, be activated and
  hold `ROLE_MEASURER` or `ROLE_ADMIN`; otherwise the family is revoked and
  the call returns `401` (deactivated/unknown) or `403` (role removed). The new
  access JWT takes its authorities from the database, so role changes apply at
  the next refresh.
- **Offline:** access-token expiry does not matter offline; on reconnect the app
  refreshes silently. Only refresh expiry or revocation requires a new login.

## API Contract

All bodies JSON. Tokens are never logged.

### Login

`POST /api/mobile/auth/login` (public)

```json
{ "username": "ana", "password": "…", "deviceId": "3f0c…-uuid", "deviceName": "Galaxy A54", "platform": "android", "appVersion": "1.0.0" }
```

- Validation (`400`): `username`/`password` required; `deviceId` UUID;
  `deviceName` ≤ 80; `platform` ∈ `android`, `ios`; `appVersion` ≤ 20.
- `200`:

```json
{ "tokenType": "Bearer", "accessToken": "jwt", "accessExpiresAt": "2026-10-10T17:00:00Z", "refreshToken": "opaque", "refreshExpiresAt": "2026-12-09T16:00:00Z" }
```

- `401` bad credentials or deactivated user; `403 MOBILE_ROLE_REQUIRED` when
  the user lacks `ROLE_MEASURER` and `ROLE_ADMIN` (#116).

### Refresh

`POST /api/mobile/auth/refresh` (public) — `{ "refreshToken": "…", "deviceId": "uuid" }`

- `200` same shape as login.
- `401` unknown, expired (sliding or absolute), revoked, reused outside the
  grace window (family revoked), or `deviceId` mismatch; `403 MOBILE_ROLE_REQUIRED` if the role was removed.

### Logout

`POST /api/mobile/auth/logout` (authenticated, `ROLE_ADMIN`/`ROLE_MEASURER`) —
`{ "deviceId": "uuid" }` → `204`; revokes the current user's family for that
device. Idempotent (unknown device → `204`).

### Admin: devices

A device = an active family.

`GET /api/admin/mobile-devices?login={login}` (`ROLE_ADMIN`) →

```json
[ { "familyId": "uuid", "login": "ana", "deviceId": "uuid", "deviceName": "Galaxy A54", "platform": "android", "appVersion": "1.0.0", "createdAt": "…", "lastUsedAt": "…", "expiresAt": "…" } ]
```

`DELETE /api/admin/mobile-devices/{familyId}` (`ROLE_ADMIN`) → `204`; `404` unknown family.

## Data Model Impact

- **Hand-written JPA entity, not JDL.** A JHipster entity always generates a
  CRUD REST endpoint and web pages; for token data that endpoint would be
  readable by every `ROLE_USER` (#142), and regenerating overwrites custom code
  (seen in #116). The entity lives outside the generator-owned `domain/`
  package: `security/mobile/MobileRefreshToken.java`.
- `MobileRefreshToken`: `id`, `tokenHash` (char 64, unique), `familyId` (UUID,
  indexed), `user` (many-to-one `User`, indexed), `deviceId` (UUID), `deviceName`,
  `platform`, `appVersion`, `issuedAt`, `expiresAt`, `familyAbsoluteExpiresAt`,
  `lastUsedAt`, `rotatedAt`, `replacedById` (self FK, nullable), `revokedAt`, `revokeReason`
  (`LOGOUT`, `ADMIN`, `REUSE`, `NEW_LOGIN`, `USER_INVALID`).
- Repository `repository/MobileRefreshTokenRepository` (pessimistic write lock on lookup by hash).
- Hand-written Liquibase changelog `YYYYMMDDHHMMSS_add_mobile_refresh_token.xml`, included in `master.xml`.
- Cleanup: daily `@Scheduled` job deletes rows whose `familyAbsoluteExpiresAt`
  or `revokedAt` is older than 30 days.

## Backend Behavior

- Extract the JWT creation from `AuthenticateController` into
  `security/JwtTokenService` (`createToken(login, userId, authorities, validity)`);
  `AuthenticateController` delegates with the same validity rules (web
  behavior and tokens unchanged).
- Resources in `web/rest/custom/`: `MobileAuthResource` (`/api/mobile/auth/**`),
  `MobileDeviceAdminResource` (`/api/admin/mobile-devices`).
- Service `MobileAuthService` (+ Impl): login via `AuthenticationManager`;
  refresh/logout/revoke in one transaction each, with a row lock on the
  presented token (prevents double use).
- Security config: `permitAll` for `POST /api/mobile/auth/login` and
  `POST /api/mobile/auth/refresh`, placed **before** `/api/mobile/**` (#116).
- Configuration `app.mobile.*` in `application.yml` (lifetimes, grace window).
- Logs: login/refresh/revoke events with login, `deviceId`, `familyId`, outcome; never tokens or hashes.

## Acceptance Criteria

- [ ] Login returns access + refresh tokens only for activated measurer/admin users (`403 MOBILE_ROLE_REQUIRED` otherwise).
- [ ] The access token works on `/api/mobile/sessions` (#116).
- [ ] Refresh rotates the token; the old token no longer works after the grace window.
- [ ] Retrying a rotated token within the grace window from the same device succeeds without revoking the family.
- [ ] Reusing a rotated token after the grace window revokes the whole family.
- [ ] A new login on the same device revokes the previous family.
- [ ] Admin revocation and logout make the next refresh fail with `401`.
- [ ] Refresh fails when the user is deactivated (`401`) or loses the mobile roles (`403`), and the family is revoked.
- [ ] Sliding expiry never exceeds the absolute expiry.
- [ ] Only SHA-256 hashes of refresh tokens are stored (DB assertion).
- [ ] Web login flow and tokens unchanged (existing `AuthenticateController`/account ITs pass).

## Test Plan

- Unit: `MobileAuthServiceImpl` with a fixed `Clock` — rotation, grace window,
  reuse, sliding vs absolute expiry, re-checks, new login on same device.
- ITs: login 200/400/401/403; refresh 200/401/403; logout 204; admin list/revoke
  200/204/404/403; access token accepted on `/api/mobile/sessions`.
- Concurrency IT: two simultaneous refreshes with the same token → one `200`,
  the other `200` via grace (same device) — never two live successors.
- Cleanup job unit test.

## Open Questions

Resolved at review (2026-10-10):

- [x] Lifetimes: 1 h access, 60 d sliding, 180 d absolute (all configurable).
- [x] Rate limiting: not in #115; required before public deployment (#142).
- [x] Entity via JDL? No — hand-written (see *Data Model Impact*).
- [x] Flaky-network retries: 30 s grace window.
