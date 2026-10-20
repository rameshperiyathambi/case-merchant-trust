#!/usr/bin/env bash
# Usage: scripts/case-context.sh "<task>" <deployment> [as-of ISO instant]
# Writes AGENT_CONTEXT.md (repo root) for the coding agent to read before editing merchant-service.
set -euo pipefail
cd "$(dirname "$0")/.."
JAR=case-context-layer/target/case-context-layer-0.1.0-SNAPSHOT.jar
[ -f "$JAR" ] || ./mvnw -q ${MVN_ARGS:-} -DskipTests package
java -jar "$JAR" --spring.main.web-application-type=none --spring.main.banner-mode=off --logging.level.root=WARN \
  --case.cli.task="$1" --case.cli.deployment="$2" --case.cli.as-of="${3:-$(date -u +%Y-%m-%dT%H:%M:%SZ)}" \
  --case.cli.out=AGENT_CONTEXT.md
