#!/usr/bin/env bash
# Applies each reference patch to a clean temporary copy of the repo and runs the merchant-service tests.
# Env: MVN_ARGS (extra Maven args, e.g. "-s /path/settings.xml").
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
for patch in "$ROOT"/experiments/agent-demo/reference-solutions/*.patch; do
  TMP="$(mktemp -d)"
  trap 'rm -rf "$TMP"' EXIT
  (cd "$ROOT" && tar --exclude=target --exclude=.git -cf - .) | tar -xf - -C "$TMP"
  (cd "$TMP" && git apply "$patch" && ./mvnw -q ${MVN_ARGS:-} -pl merchant-service -am test)
  echo "OK: $(basename "$patch")"
  rm -rf "$TMP"
done
