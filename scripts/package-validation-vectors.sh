#!/usr/bin/env bash
# Packages the validation conformance vectors (E12 #113) with the catalog they
# were written for, so the KFS-APP CI can run them against the Dart engine.
#
# Output: build/validation-vectors/validation-vectors-<catalogVersion>.zip
#         (+ .sha256), containing vectors/*.json, catalog.json, manifest.json
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
CATALOG="$ROOT/src/main/resources/croquis/catalog.json"
VECTORS="$ROOT/src/test/resources/site-measurement/validation-vectors"
OUT="$ROOT/build/validation-vectors"

read_json() { python3 -c "import json,sys; print(json.load(open(sys.argv[1]))[sys.argv[2]])" "$CATALOG" "$1"; }
CATALOG_VERSION="$(read_json catalogVersion)"
ENGINE_VERSION="$(read_json rulesEngineVersion)"
NAME="validation-vectors-${CATALOG_VERSION}"

rm -rf "$OUT/$NAME" && mkdir -p "$OUT/$NAME/vectors"
cp "$VECTORS"/*.json "$OUT/$NAME/vectors/"
cp "$CATALOG" "$OUT/$NAME/catalog.json"

python3 - "$OUT/$NAME" "$CATALOG_VERSION" "$ENGINE_VERSION" <<'PY'
import hashlib, json, os, sys
root, catalog_version, engine_version = sys.argv[1], sys.argv[2], int(sys.argv[3])
files = {}
for dirpath, _, names in os.walk(root):
    for name in sorted(names):
        path = os.path.join(dirpath, name)
        rel = os.path.relpath(path, root)
        with open(path, "rb") as fh:
            files[rel] = hashlib.sha256(fh.read()).hexdigest()
manifest = {
    "catalogVersion": catalog_version,
    "rulesEngineVersion": engine_version,
    "vectorCount": sum(1 for f in files if f.startswith("vectors/")),
    "files": dict(sorted(files.items())),
}
with open(os.path.join(root, "manifest.json"), "w") as fh:
    json.dump(manifest, fh, indent=2)
    fh.write("\n")
PY

python3 - "$OUT" "$NAME" <<'PY'
import os, sys, zipfile
out, name = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(os.path.join(out, name + ".zip"), "w", zipfile.ZIP_DEFLATED) as zf:
    for dirpath, _, names in os.walk(os.path.join(out, name)):
        for n in sorted(names):
            path = os.path.join(dirpath, n)
            zf.write(path, os.path.relpath(path, out))
PY
(cd "$OUT" && sha256sum "$NAME.zip" > "$NAME.zip.sha256")
echo "$OUT/$NAME.zip"
