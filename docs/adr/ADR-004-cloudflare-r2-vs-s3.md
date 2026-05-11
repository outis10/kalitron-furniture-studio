# ADR-004: Use Cloudflare R2 for Object Storage Instead of AWS S3

## Status

Accepted

## Context

The application handles user-uploaded reference photos, generated renders, sketches, PDFs, CSV exports, BOM JSON, and Fusion 360 related artifacts.

These files should not be stored as Base64 or blobs in chat messages. The database should store metadata, ownership, type, paths, and references through entities such as `DesignImage` and `DesignArtifact`.

Object storage is needed for:

- Reference images uploaded by clients.
- AI-generated renders.
- Proposal PDFs.
- CSV files for Fusion 360 scripts.
- Future STEP, DXF, model, or BOM exports.

AWS S3 is a standard option, but Cloudflare R2 provides an S3-compatible API and is attractive for a demo or early product because of simpler cost expectations around egress and broad compatibility with existing S3 client patterns.

## Decision

Use Cloudflare R2 as the preferred object storage target.

The application should interact with storage through an abstraction rather than coupling business code directly to R2-specific APIs.

Persist file metadata in PostgreSQL:

- `DesignImage` for reference images, renders, catalog thumbnails, and sketches.
- `DesignArtifact` for CSV, Fusion scripts, PDFs, STEP, DXF, BOM JSON, and related outputs.

Store only paths, object keys, file names, MIME types, sizes, checksums, and metadata JSON in the database.

## Consequences

The database remains focused on structured domain data.

Large binary payloads do not bloat `ChatMessage` or domain aggregate tables.

The system can later swap R2 for S3-compatible alternatives if needed.

R2 credentials and bucket configuration must be provided through environment variables or deployment secrets, never committed to the repository.

Local development can use filesystem storage or a local S3-compatible service if R2 is not available.

Public proposal links must not expose raw private object keys directly; access should be mediated by application-level authorization or signed URLs.

