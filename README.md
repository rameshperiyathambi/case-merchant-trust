# CASE sample: operational validity for coding-agent context (synthetic)

> License pending legal review (see `LICENSE.TODO`).

Artifact for the paper "When Relevant Code Is Wrong: Runtime-Aware Context for AI Coding Agents". It reproduces the
paper's failure mode in a small, fully synthetic Java 17 / Spring Boot 3.5 system: a retrieved artifact can be topically
relevant and historically correct, yet wrong for the target deployment. It also contains a minimal CASE context layer
that labels evidence `VALID`, `INVALID` or `UNKNOWN` for a deployment and time, and emits bounded, source-backed
context packages for an unmodified coding agent. All names and data are fictional; the system only resembles real
marketplace platforms.

## Quickstart

```bash
./mvnw verify                      # build + all tests (Java 17+)
scripts/case-context.sh "Extend the merchant trust score with a new return rate signal" eu-prod 2026-06-01T00:00:00Z
cat AGENT_CONTEXT.md               # ACT ON / VERIFY / EXPLAIN package for the agent
scripts/run-pilot.sh               # pilot harness -> pilot/out/ (frozen run: pilot/results/)
```

If your Maven mirror cannot reach Maven Central, pass `MVN_ARGS="-s <settings.xml>"` to the scripts, or
`-s <settings.xml>` to `./mvnw`.

## The scenario

The target system serves a merchant trust score through a legacy REST endpoint and a newer GraphQL path. Feature flags
decide which is live, per deployment and over time.

| Deployment | State at 2026-06-01 | REST | GraphQL |
|---|---|---|---|
| `us-prod` | migrated 2026-01-15 | INVALID | VALID |
| `eu-prod` | migrated 2026-01-20, rolled back 2026-04-02 (PR-231) | VALID | INVALID |
| `apac-prod` | GraphQL only for an unverified cohort | VALID | UNKNOWN |

`eu-prod` at 2026-03-01 behaves like `us-prod`. PR-245 is the most recent change, but its flag is off everywhere, so
recency alone is misleading.

## How CASE decides (`ApplicabilityEvaluator`, paper Eq. 1)

- Authoritative, deployment-wide guard flag off: `INVALID`. On with no active supersession: `VALID`.
- Flag on but a supersession record is active: conflict, `UNKNOWN`.
- Cohort-scoped, non-authoritative or missing evidence: `UNKNOWN` (never inferred invalid), unless an active
  supersession says otherwise.
- Only evidence timestamped at or before `t` is used. Invalid artifacts appear only as labeled history (EXPLAIN).

The package has three sections: ACT ON (valid prescriptive artifacts), VERIFY (unknowns with an obligation), EXPLAIN
(rationale and retired implementations, explicitly not a blueprint). If nothing valid exists, it says so.

## Repository map

| Path | Contents |
|---|---|
| `merchant-service/` | Target system (REST + GraphQL, flag router) |
| `feature-flags/` | Flag snapshot library and `GET /flags?deployment=&asOf=` |
| `case-context-layer/` | CASE: retrieval, evidence graph, applicability, packaging, conditions, API, CLI, pilot |
| `flags/`, `context/`, `history/` | Synthetic flag snapshots, lineage, artifact catalog, PRs, ADR |
| `pilot/` | Tasks, oracles, annotation schema, frozen results |
| `experiments/agent-demo/` | Agent run protocol, prompt generator, reference solutions |
| `docs/` | See the documentation table below |

## Documentation

| Document | Read it for |
|---|---|
| [`docs/walkthrough.md`](docs/walkthrough.md) | The paper's failure mode, step by step, with real package output and the history timeline |
| [`docs/architecture.md`](docs/architecture.md) | Modules, packages and the paper-to-code map |
| [`docs/metrics.md`](docs/metrics.md) | Exact definitions of every pilot metric |
| [`docs/reference.md`](docs/reference.md) | Scripts, configuration properties, HTTP interfaces, conditions, glossary |
| [`docs/data-formats.md`](docs/data-formats.md) | File formats and how to add deployments, artifacts and tasks |
| [`pilot/annotation-schema.md`](pilot/annotation-schema.md) | RQ1 labelling instructions |
| [`experiments/agent-demo/protocol.md`](experiments/agent-demo/protocol.md) | Running the agent experiment |
| [`docs/release-checklist.md`](docs/release-checklist.md) | Publishing and anonymization steps |

## Evaluation support

- **RQ2 conditions** (shared pool and token budget): `SEMANTIC_ONLY`, `RECENCY_FILTERED` (180-day window),
  `SEMANTIC_PLUS_RAW_CONFIG` (adds the raw flag snapshot, unadjudicated), `CASE_VALIDITY_PACKAGE`.
- **RQ3 ablations**: `CASE_NO_FLAG_EVIDENCE`, `CASE_NO_LINEAGE_EVIDENCE`; plus the two-deployment replay probe.
- **RQ1**: the pilot writes `pool.csv` per task for two annotators; `pilot/annotations/` files are analysed for
  precision@k and Cohen's kappa (`pilot/annotation-schema.md`).
- **Agent runs**: `experiments/agent-demo/protocol.md`; reference patches for `us-prod` (GraphQL) and `eu-prod`
  (REST) are checked in CI.

Frozen pilot output: [`pilot/results/summary.md`](pilot/results/summary.md).

## Limitations (read before citing numbers)

- All data is synthetic and small (15 artifacts, 3 deployments, 6 pilot tasks). Results are illustrative, not evidence
  of effectiveness on real systems.
- Oracles are hand-written by the authors from the same rules the evaluator implements, so CASE's precision is high by
  construction. The meaningful signals are the baselines' failures, the ablation costs and the replay probe.
- Lineage evidence is redundant in this fixture (flags decide every case), so its ablation shows no effect.
- Artifact-to-implementation links and preconditions are expert-curated lookups, not automatically recovered.
- Token counts approximate characters / 4. BM25 is a simple lexical baseline, not a state-of-the-art retriever.
- No agent runs are bundled; the kit supports running them (Cursor, Claude Code, etc.).

## Using the API

```bash
java -jar case-context-layer/target/case-context-layer-0.1.0-SNAPSHOT.jar   # serves :8081
curl -s -X POST 'localhost:8081/context-packages?format=markdown' -H 'Content-Type: application/json' \
  -d '{"task":"Add a signal to the trust score","deployment":"us-prod","asOf":"2026-06-01T00:00:00Z","condition":"CASE_VALIDITY_PACKAGE","budgetTokens":1500}'
java -jar merchant-service/target/merchant-service-0.1.0-SNAPSHOT.jar --merchant.deployment=eu-prod --merchant.as-of=2026-06-01T00:00:00Z
```

## Agent demo

`AGENTS.md` (and `.cursor/rules/case-context.mdc`) instruct an agent to generate and follow `AGENT_CONTEXT.md` before
editing `merchant-service`. Run the same task for `eu-prod` with and without it; see the experiment protocol for the
controlled version.

## How to cite

To be added after acceptance (see `docs/release-checklist.md`).
