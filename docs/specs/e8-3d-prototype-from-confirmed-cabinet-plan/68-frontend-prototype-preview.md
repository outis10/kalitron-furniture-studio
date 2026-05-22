# E8 Issue 68: Frontend Prototype Preview and Status Panel

Status: Implemented
Issue: #68
Epic: #64
Depends on: #66, #67

## Problem

The designer needs to see the generated floor plan prototype from the design chat
UI, trigger regeneration, and understand the job status — all without leaving the
session chat view.

## Goal

Add a prototype panel in `design-chat.tsx` that shows the current prototype status,
displays the floor plan thumbnail (click to expand), and exposes a generate/regenerate
button. The panel is only shown when a confirmed cabinet plan exists.

## UX States

| State | Display |
|---|---|
| No cabinet plan | Panel hidden |
| Cabinet plan exists, no prototype yet | "Generar prototipo 2D" button |
| Generating | Button disabled with spinner + "Generando..." label |
| Done — preview available | Thumbnail (click to expand full-size in new tab) + "Regenerar" button |
| Done — preview not available | Status text only + "Regenerar" button |
| Failed | Error message + "Reintentar" button |

## API Functions Added (`design-chat-api.ts`)

```ts
generatePrototype(sessionId: number): Promise<Prototype3dJob>
getLatestPrototypeJob(sessionId: number): Promise<Prototype3dJob | null>
getPrototypePreview(sessionId: number): Promise<string | null>  // returns blob URL
```

## Session Resume Behavior

On session resume (`useEffect` with sessionCode):
1. `loadPersistedArtifacts` runs `getMeasuredLayout`, `getCabinetPlan`, `getSketchImage` in parallel.
2. After resolving, `getLatestPrototypeJob` is called.
3. If job exists and `status === 'DONE'`, `getPrototypePreview` is called to fetch the blob URL.
4. `prototypeJob` and `prototypePreviewUrl` states are set accordingly.

## Blob URL Memory Management

- `prototypePreviewUrl` blob URL is revoked in `handleRestart` via `URL.revokeObjectURL`.
- `savedSketchPreviewUrl` blob URL is also revoked in `handleRestart`.

## New State Variables

```ts
prototypeJob: Prototype3dJob | null
prototypePreviewUrl: string | null
isGeneratingPrototype: boolean
```

## Module-Level Renderer

`renderPrototypeSection(savedCabinetPlanInfo, prototypeJob, prototypePreviewUrl, isGeneratingPrototype, handleGeneratePrototype)` — keeps component branch count below ESLint complexity limit of 40.

## Acceptance Criteria

- [x] Prototype panel is hidden when no cabinet plan exists.
- [x] "Generar prototipo 2D" button visible when cabinet plan exists but no prototype.
- [x] Button shows spinner and is disabled while `isGeneratingPrototype` is true.
- [x] On success, floor plan thumbnail rendered from blob URL.
- [x] Thumbnail click opens full-size image in new tab.
- [x] On error, error message displayed with retry button.
- [x] On session resume, existing prototype job and preview are restored without regeneration.
- [x] Blob URL revoked when session is restarted.
- [x] ESLint complexity passes (max 40 branches).
