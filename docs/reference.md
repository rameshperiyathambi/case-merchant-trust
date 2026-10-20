# Reference: configuration, interfaces and glossary

## Scripts

| Script | Purpose |
|---|---|
| `scripts/case-context.sh "<task>" <deployment> [as-of]` | Writes `AGENT_CONTEXT.md` (CASE package) at the repo root |
| `scripts/run-pilot.sh [out-dir]` | Runs the pilot; default output `pilot/out/` |
| `scripts/check-results.sh` | Regenerates the pilot and fails if it differs from `pilot/results/` |
| `experiments/agent-demo/scripts/make-prompts.sh "<task>" <deployment> [as-of]` | Prompt per condition plus a randomized run order |
| `experiments/agent-demo/scripts/check-reference-solutions.sh` | Applies each reference patch to a temp copy and runs tests |

All scripts accept `MVN_ARGS` (e.g. `-s <settings.xml>`) when they need to build.

## Configuration properties

| Property | Used by | Default | Meaning |
|---|---|---|---|
| `case.repo-root` | all modules | nearest ancestor with `flags/` and `context/` | Repository root for data files |
| `merchant.deployment` | merchant-service | `us-prod` | Deployment whose flags route requests |
| `merchant.as-of` | merchant-service | now | Evaluate flags as of this instant (for demos and tests) |
| `case.cli.task` | case CLI | (required) | Task text; enables CLI mode |
| `case.cli.deployment` | case CLI | (required) | Target deployment |
| `case.cli.as-of` | case CLI | now | Snapshot time t |
| `case.cli.condition` | case CLI | `CASE_VALIDITY_PACKAGE` | Any `Condition` value |
| `case.cli.out` | case CLI | `AGENT_CONTEXT.md` | Output file |
| `case.pilot.run` | pilot | unset | `true` runs the pilot |
| `case.pilot.out` | pilot | `pilot/out` | Output directory |

Pass properties as `--name=value` to `java -jar`. Use `--spring.main.web-application-type=none` for CLI and pilot runs.

## HTTP interfaces

| Service (port) | Endpoint | Notes |
|---|---|---|
| merchant-service (8080) | `GET /api/v1/merchants/{id}/trust-score` | Legacy REST; 410 Gone when REST is not live for the cohort |
| merchant-service (8080) | `POST /graphql`, query `merchant(id) { trustScore { score signals { name weight value } } }` | Errors when GraphQL is not live |
| merchant-service (8080) | `GET /flags?deployment=&asOf=` | Flag snapshot at time t |
| case-context-layer (8081) | `POST /context-packages?format=json\|markdown` | Body below |
| case-context-layer (8081) | `GET /flags?deployment=&asOf=` | Same flag endpoint |

Both trust-score paths read the optional `X-Cohort` header (default `default`); `apac-prod` enables GraphQL only for
`beta-merchants`.

`POST /context-packages` body: `task` (required), `deployment` (required), `asOf` (default now), `condition`
(default `CASE_VALIDITY_PACKAGE`), `budgetTokens` (default 1500), `topK` (default 8).

## Conditions

| Condition | What the agent receives |
|---|---|
| `SEMANTIC_ONLY` | Top-k artifacts by relevance |
| `RECENCY_FILTERED` | Pool artifacts updated within 180 days before t, newest first |
| `SEMANTIC_PLUS_RAW_CONFIG` | Raw flag snapshot for (d, t) plus the top-k artifacts, unadjudicated |
| `CASE_VALIDITY_PACKAGE` | ACT ON / VERIFY / EXPLAIN with evidence |
| `CASE_NO_FLAG_EVIDENCE` | CASE without flag evidence (RQ3) |
| `CASE_NO_LINEAGE_EVIDENCE` | CASE without supersession records (RQ3) |

## Glossary: paper to repository

| Paper | Repository |
|---|---|
| Task q | `TaskRequest.task` |
| Deployment d | `TaskRequest.deployment`; a directory under `flags/` |
| Snapshot time t | `TaskRequest.asOf`; only evidence at or before t is used |
| Artifact a | An entry in `context/artifacts.yaml` (`model.Artifact`) |
| Relevance R(a, q) | BM25 score (`retrieval.Bm25Retriever`) |
| Preconditions π_a | The artifact's `implementation` -> `requiresFlag` in `context/lineage.yaml`, plus its supersession records |
| Evidence E_t | Flag snapshots and lineage records as of t (`graph.EvidenceGraph`, `model.Evidence`) |
| V(a \| d, t) | `applicability.ApplicabilityEvaluator` -> `model.Applicability` (VALID, INVALID, UNKNOWN) |
| Prescriptive vs explanatory | `role` in `artifacts.yaml` (`model.ArtifactRole`) |
| Package P_B(q, d, t) | `packaging.ContextPackage`; budget B = `budgetTokens` |
| Act on / Explain / Verify | `SectionKind.ACT_ON`, `EXPLAIN`, `VERIFY` |
