# Pilot metrics

Definitions of the numbers in `pilot/results/summary.csv` and `summary.md`, as computed by
`case-context-layer/.../pilot/PilotRunner.java` and `AnnotationAnalyzer.java`.

## Key terms

- **Pool**: the top-k artifacts (k = 8) by BM25 relevance for the task, restricted to artifacts that existed at `t`.
  Every condition for a task gets the same pool and the same token budget (1500 approx. tokens).
- **Actionable**: the artifacts a package presents as something the agent may act on or copy from.
  - CASE conditions: only the ACT ON section. VERIFY and EXPLAIN items are not actionable.
  - Baselines: every retrieved artifact (they have no sections). The raw flag snapshot in `SEMANTIC_PLUS_RAW_CONFIG`
    is not an artifact and is not counted.
- **Oracle**: hand-written labels in `pilot/oracles/<key>.yaml` listing which artifacts are `applicable`,
  `incompatible` or `unknown` for the task's deployment and time. Never passed to any strategy.

## `summary.csv` columns (one row per task and condition)

| Column | Definition |
|---|---|
| `task`, `stratum`, `deployment` | From `pilot/tasks/<id>.yaml`; stratum is `migration`, `flag-region` or `stable` |
| `condition` | One of the six conditions |
| `actionable` | Number of actionable artifacts |
| `valid_precision` | actionable and applicable, divided by actionable (0 when nothing is actionable) |
| `valid_recall` | actionable and applicable, divided by the number of applicable artifacts in the pool (0 if none) |
| `incompatible` | Number of actionable artifacts the oracle labels incompatible: the paper's primary failure |
| `unknown_actionable` | Number of actionable artifacts whose status the oracle says cannot be established |
| `tokens` | Approximate package size (characters / 4, plus a fixed 60-token header estimate) |

## `summary.md`

- Mean valid precision and mean valid recall: unweighted means over the six tasks. A task with nothing actionable
  contributes 0 (this is what pulls `CASE_NO_FLAG_EVIDENCE` down).
- Incompatible actionable and unknown actionable: totals over the six tasks.
- Replay probe: for tasks with `replayDeployment`, the CASE ACT ON set under both deployments; `changed: true` means
  the prescriptive evidence followed the deployment.

## RQ1 annotation metrics (only when `pilot/annotations/*.csv` exist)

| Metric | Definition |
|---|---|
| k | Rows in the task's annotation file |
| agreed | Rows where both annotators gave the same label |
| valid precision@k | agreed `APPLICABLE` rows divided by k |
| incompatible@k | agreed `INCOMPATIBLE` rows divided by k |
| agreement | Share of agreeing rows over all tasks |
| Cohen's kappa | Over all rows of all tasks, three labels; 1.0 if both annotators used a single identical label |

## How to read the frozen results

CASE's precision is high by construction, because the oracles follow the rules the evaluator implements. The
informative comparisons are:

- how many incompatible artifacts each baseline presents as actionable;
- what each ablation costs in recall;
- whether the replay probe changes with the deployment.
