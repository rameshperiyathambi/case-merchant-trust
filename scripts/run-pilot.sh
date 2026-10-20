#!/usr/bin/env bash
# Runs the read-only pilot harness. Usage: scripts/run-pilot.sh [output dir, default pilot/out]
# Env: MVN_ARGS (extra Maven args when the jar must be built).
set -euo pipefail
cd "$(dirname "$0")/.."
JAR=case-context-layer/target/case-context-layer-0.1.0-SNAPSHOT.jar
[ -f "$JAR" ] || ./mvnw -q ${MVN_ARGS:-} -DskipTests package
java -jar "$JAR" --spring.main.web-application-type=none --spring.main.banner-mode=off --logging.level.root=WARN \
  --case.pilot.run=true --case.pilot.out="${1:-pilot/out}"
