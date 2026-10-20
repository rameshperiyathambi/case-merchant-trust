#!/usr/bin/env bash
# Usage: make-prompts.sh "<task>" <deployment> [as-of]
# Writes prompts/<deployment>/<condition>.md (task + context package per condition) and a shuffled run order.
# Feed each prompt to the SAME frozen agent/model in a fresh session; do not tell it the condition name.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../../.." && pwd)"
TASK="$1"; DEPLOY="$2"; ASOF="${3:-2026-06-01T00:00:00Z}"
JAR="$ROOT/case-context-layer/target/case-context-layer-0.1.0-SNAPSHOT.jar"
[ -f "$JAR" ] || (cd "$ROOT" && ./mvnw -q ${MVN_ARGS:-} -DskipTests package)
OUT="$ROOT/experiments/agent-demo/prompts/$DEPLOY"; mkdir -p "$OUT"
CONDITIONS="SEMANTIC_ONLY RECENCY_FILTERED SEMANTIC_PLUS_RAW_CONFIG CASE_VALIDITY_PACKAGE CASE_NO_FLAG_EVIDENCE CASE_NO_LINEAGE_EVIDENCE"
for C in $CONDITIONS; do
  java -jar "$JAR" --spring.main.web-application-type=none --spring.main.banner-mode=off --logging.level.root=WARN \
    --case.cli.task="$TASK" --case.cli.deployment="$DEPLOY" --case.cli.as-of="$ASOF" --case.cli.condition="$C" \
    --case.cli.out="$OUT/$C.context.md" >/dev/null
  { echo "Implement the following change in this repository. Produce a plan and a patch; do not deploy anything."
    echo; echo "Task: $TASK"; echo "Target deployment: $DEPLOY (as of $ASOF)"; echo
    echo "Use only the context below as your evidence about the system."; echo
    cat "$OUT/$C.context.md"; } > "$OUT/$C.md"
  rm "$OUT/$C.context.md"
done
echo $CONDITIONS | tr ' ' '\n' | sort -R > "$OUT/run-order.txt"
echo "Prompts in $OUT (run order: $OUT/run-order.txt)"
