# RQ1 annotation schema

For each task, `pilot/results/<task>/pool.csv` (also written to `pilot/out/` on every run) lists the shared top-k
pool. Two engineers label every artifact independently, without seeing CASE's output, and save
`pilot/annotations/<task>.csv`:

```
artifact_id,annotatorA,annotatorB
code-aggregator,APPLICABLE,APPLICABLE
```

Labels (prescriptive use for the task's target deployment `d` at time `t`):

| Label | Meaning |
|---|---|
| `APPLICABLE` | The artifact's implementation is live for `d` at `t`; it may be used as a blueprint. |
| `INCOMPATIBLE` | The implementation is retired, disabled, or otherwise not live for `d` at `t`. |
| `UNKNOWN` | Available evidence cannot establish either (cohort-scoped, missing, or conflicting). |

Allowed evidence: flag snapshots in `flags/`, `context/lineage.yaml`, and history in `history/`, as available at `t`.
Absence of runtime traces is not evidence of `INCOMPATIBLE`. Explanatory artifacts (ADR, migration note, rollback PR)
are labelled for prescriptive use too. `scripts/run-pilot.sh` then reports valid precision@k, incompatible@k (on
agreed labels) and Cohen's kappa in `summary.md`.
