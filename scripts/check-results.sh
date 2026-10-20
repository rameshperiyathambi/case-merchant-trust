#!/usr/bin/env bash
# Regenerates the pilot output and fails if it differs from the frozen pilot/results/ (reproducibility check).
set -euo pipefail
cd "$(dirname "$0")/.."
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
scripts/run-pilot.sh "$TMP" >/dev/null
diff -r -x RUN.md pilot/results "$TMP" && echo "pilot results reproduce"
