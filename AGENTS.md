# Agent instructions: merchant-service

This repository intentionally contains BOTH a legacy REST trust-score path and a newer GraphQL path.
Which one is correct depends on the target deployment and time. Do not choose by similarity, file age, or
"most recent PR".

Before editing `merchant-service`:

1. Identify the target deployment (`us-prod`, `eu-prod`, `apac-prod`) and the snapshot time. Ask if unknown.
2. Generate context: `scripts/case-context.sh "<task>" <deployment> [as-of]`, then read `AGENT_CONTEXT.md`.
3. Implement only against artifacts under **ACT ON**. Treat **EXPLAIN** as history, never as a blueprint.
   For anything under **VERIFY**, state the open question and ask the user instead of assuming.
4. If the package reports that no valid pattern exists, stop and request a deployment-specific check.
5. Cite the artifact pointers and evidence timestamps your plan relies on.

Build and test: `./mvnw verify` (Java 17+). All data in this repo is synthetic.
