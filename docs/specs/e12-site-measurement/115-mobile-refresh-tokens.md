# [E12] Issue 115: Mobile Refresh Tokens (Rotating, per Device, Revocable)

Status: Draft
Issue: #115
Epic: #104
Related: outis10/KFS-APP#6 (mobile auth), #116 (measurer role)
Owner: TBD

## Problem

Studio issues stateless JWTs (24 h, or 30 days with `rememberMe`) and has no
refresh or revocation. For a field app that is a bad trade-off:

- a 30-day bearer token on a phone cannot be revoked if the phone is lost;
- a short token forces designers to type passwords on site, often offline.

## Decision

**Recommended: add refresh tokens for mobile only.** Short-lived access JWT +
long-lived, rotating, revocable refresh token bound to a device. The web login
stays exactly as it is.

## Goal

Designers log in once per device and stay logged in while they keep using the
app, while admins can revoke a lost device immediately.

## Non-Goals

- Changing the web (`/api/authenticate`) flow.
- OAuth2/OIDC server migration.

## Token policy

| Token | Format | Lifetime | Storage |
| --- | --- | --- | --- |
| Access | Existing JHipster JWT | 1 h (configurable `app.mobile.access-token-seconds`) | Memory + secure storage on device |
| Refresh | Opaque random 256-bit, base64url | 60 days sliding, 180 days absolute (configurable) | Device: Keychain/Keystore. Server: **SHA-256 hash only** |

- Rotation: every refresh returns a new refresh token and invalidates the old one.
- Reuse detection: presenting an already-rotated token revokes the whole
  token family for that device (possible theft).
- Offline: access-token expiry does not matter offline; on reconnect the app
  refreshes silently. Only a refresh-token expiry or revocation requires login.

## API Contract

`POST /api/mobile/auth/login` (public)

```json
{ "username": "ana", "password": "…", "deviceId": "uuid", "deviceName": "Galaxy A54", "platform": "android", "appVersion": "1.0.0" }
```

`200`: `{ "accessToken": "jwt", "accessExpiresAt": "…", "refreshToken": "opaque", "refreshExpiresAt": "…" }`
Errors: `401` bad credentials, `403` user lacks `ROLE_MEASURER`/`ROLE_ADMIN` (#116), `400` validation.

`POST /api/mobile/auth/refresh` (public) — `{ "refreshToken": "…", "deviceId": "uuid" }`
→ `200` same shape as login; `401` expired/revoked/reused (family revoked).

`POST /api/mobile/auth/logout` (authenticated) — `{ "deviceId": "uuid" }` → `204`, revokes device tokens.

`GET /api/admin/mobile-devices` / `DELETE /api/admin/mobile-devices/{id}` (ROLE_ADMIN)
— list devices per user (name, platform, appVersion, lastUsedAt) and revoke.

## Data Model Impact

- New entity `MobileRefreshToken` (via JDL): `tokenHash` (unique), `familyId`,
  `deviceId`, `deviceName`, `platform`, `appVersion`, `issuedAt`, `expiresAt`,
  `absoluteExpiresAt`, `lastUsedAt`, `revokedAt`, `replacedByHash`;
  many-to-one `User`.
- New Liquibase changelog, included in `master.xml`.
- Cleanup job deletes rows expired > 30 days.

## Backend Behavior

- Resource: `web/rest/custom/MobileAuthResource`; service `MobileAuthService`.
- Reuses `AuthenticationManager` and the existing JWT encoder for access tokens.
- Refresh in one transaction with row lock on the token (prevents double use).
- Security config: permit `/api/mobile/auth/login` and `/api/mobile/auth/refresh`.
- Rate-limit login/refresh per IP + username (open question on mechanism).
- Never log tokens.

## Acceptance Criteria

- [ ] Login returns access + refresh tokens only for measurer/admin users.
- [ ] Refresh rotates the token; old token no longer works.
- [ ] Reusing a rotated token revokes all tokens of that device family.
- [ ] Admin revocation makes the next refresh fail with `401`.
- [ ] Only hashes of refresh tokens are stored.
- [ ] Web login flow unchanged (existing tests pass).

## Test Plan

- Backend: service unit tests (rotation, reuse, expiry, absolute expiry);
  resource ITs (200/401/403); concurrency test for double refresh.

## Open Questions

- [ ] Final lifetimes (1 h / 60 d / 180 d proposed).
- [ ] Rate limiting: Bucket4j, reverse proxy, or none for internal pilot?
