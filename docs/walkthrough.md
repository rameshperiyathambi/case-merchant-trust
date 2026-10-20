# Walkthrough: when relevant code is wrong

This walks through the paper's motivating failure on the synthetic system, using the frozen pilot output in
`pilot/results/`. All data is fictional.

## 1. The history the agent can see

| Date | Event | Where it is recorded |
|---|---|---|
| 2024-11-10 | REST trust-score endpoint added (PR-101) | `history/prs/PR-101-rest-trust-score.md` |
| 2025-09-01 | All deployments serve REST | `flags/*/2025-09-01.yaml` |
| 2025-12-01 | ADR-007: move to GraphQL, per deployment | `history/adr/ADR-007-graphql.md` |
| 2026-01-12 | GraphQL trust score with pluggable signals (PR-214) | `history/prs/PR-214-graphql-migration.md` |
| 2026-01-15 | `us-prod` switches to GraphQL; REST off | `flags/us-prod/2026-01-15.yaml`, `context/lineage.yaml` |
| 2026-01-20 | `eu-prod` switches to GraphQL; REST off | `flags/eu-prod/2026-01-20.yaml`, `context/lineage.yaml` |
| 2026-02-01 | `apac-prod`: GraphQL for an unverified cohort only; REST on for all | `flags/apac-prod/2026-02-01.yaml` |
| 2026-04-02 | `eu-prod` rolls back to REST (PR-231) | `flags/eu-prod/2026-04-02.yaml`, `history/prs/PR-231-eu-rollback.md` |
| 2026-05-20 | Experimental return-rate signal, flag off everywhere (PR-245) | `history/prs/PR-245-experimental-signal.md` |

The pilot tasks use `t = 2026-06-01` unless noted; `t2` uses `t = 2026-03-01`, before the EU rollback.

## 2. The task

> Extend the merchant trust score with a new return rate signal. Target: `eu-prod`, as of 2026-06-01.

The correct change for `eu-prod` at that time is in the legacy REST formula, because of the rollback. The same task
for `us-prod` must go to GraphQL instead. Both reference solutions are in
`experiments/agent-demo/reference-solutions/` (`eu-prod-rest.patch`, `us-prod-graphql.patch`).

## 3. What similarity retrieval gives the agent

`pilot/results/t3-flag-eu-after-rollback/semantic_only.md` ranks the GraphQL migration PR first, then the REST PR,
then the dormant experiment PR. It then mixes REST and GraphQL code with no indication of which is live:

```
- history/prs/PR-214-graphql-migration.md        (GraphQL; disabled in eu-prod)
- history/prs/PR-101-rest-trust-score.md         (REST; live in eu-prod)
- history/prs/PR-245-experimental-signal.md      (flag off everywhere)
- .../rest/LegacyTrustScoreService.java
- .../TrustSignalAggregatorTest.java             (GraphQL)
- ...
```

Every item is topically relevant and correct for some deployment or time. Four of the eight are wrong for `eu-prod`
today. Recency filtering is worse here: it keeps only the four newest artifacts, and all four are incompatible.

## 4. What CASE gives the agent

`pilot/results/t3-flag-eu-after-rollback/case_validity_package.md` separates the same pool by applicability:

```
ACT ON  [VALID]   PR-101, LegacyTrustScoreService, its test, LegacyTrustScoreController
        Evidence: flags/eu-prod/2026-04-02.yaml (trust-score.rest.enabled on for all traffic)
                  history/prs/PR-231-eu-rollback.md (supersession by GraphQL reverted in eu-prod)
EXPLAIN [INVALID] PR-214, TrustSignalAggregator and its test   (trust-score.graphql.enabled off)
        [INVALID] PR-245                                       (experiment flag off)
```

The GraphQL artifacts are not deleted. They stay visible as labelled history, so the agent can explain why REST is
used, but it is told not to copy them.

## 5. The same task in other deployments

| Deployment, t | CASE ACT ON | CASE VERIFY |
|---|---|---|
| `us-prod`, 2026-06-01 (`t1`) | GraphQL aggregator, its test, PR-214 | none |
| `eu-prod`, 2026-03-01 (`t2`, before rollback) | GraphQL aggregator, resolver, test, PR-214 | none |
| `eu-prod`, 2026-06-01 (`t3`) | REST service, controller, test, PR-101 | none |
| `apac-prod`, 2026-06-01 (`t4`) | REST service, test, PR-101 | GraphQL aggregator, its test, PR-214 (cohort-only, unverified) |

The `t2` and `t4` task wording differs slightly from `t1`/`t3`, so their pools differ a little.

The replay probe in `pilot/results/summary.md` checks this automatically: the same task and repository state give
different ACT ON sets for `us-prod` and `eu-prod`.

## 6. Try it

```bash
./mvnw verify
scripts/case-context.sh "Extend the merchant trust score with a new return rate signal" eu-prod 2026-06-01T00:00:00Z
scripts/case-context.sh "Extend the merchant trust score with a new return rate signal" us-prod 2026-06-01T00:00:00Z
```

Compare the two `AGENT_CONTEXT.md` outputs, then give the same task to a coding agent with and without the package
(`experiments/agent-demo/protocol.md`).
