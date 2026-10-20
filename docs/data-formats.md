# Data formats

## Flag snapshot: `flags/<deployment>/<YYYY-MM-DD>.yaml`
```yaml
deployment: eu-prod
effectiveAt: "2026-04-02T00:00:00Z"
authority: AUTHORITATIVE        # anything else makes every flag UNKNOWN
flags:
  trust-score.graphql.enabled:
    enabled: false
    scope: ALL                  # or COHORT (then: cohorts: [..], assignmentVerified: bool)
```
A snapshot applies from `effectiveAt` until the next snapshot of the same deployment.

## `context/lineage.yaml`
`implementations.<name>.requiresFlag` links a runtime path to its guard flag. `supersessions[]` records
`implementation` superseded by `supersededBy` per deployment with `effectiveAt`, optional `revertedAt` and `revertSource`.

## `context/artifacts.yaml`
Per artifact: `id`, `type`, `role` (PRESCRIPTIVE | EXPLANATORY), `implementation` (a name from lineage, or `none` for
expert-reviewed deployment-independent artifacts), `updatedAt`, `path` (repo-relative), `title`, `summary`.

## Pilot: `pilot/tasks/<id>.yaml` and `pilot/oracles/<key>.yaml`
Task: `id`, `stratum`, `task`, `deployment`, `asOf`, `oracle` (key), optional `replayDeployment`.
Oracle: lists `applicable`, `incompatible`, `unknown` of artifact ids. Oracles are never given to a strategy.

## How to extend
- New deployment: add `flags/<deployment>/<date>.yaml` (and lineage entries if it migrated).
- New artifact: add the file, add an entry in `artifacts.yaml`; reference an existing `implementation` or add one.
- New task: add a task file and an oracle key; re-run `scripts/run-pilot.sh pilot/results` and commit.
