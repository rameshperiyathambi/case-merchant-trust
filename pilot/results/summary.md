# Pilot summary (synthetic data; illustrative, not a result)

Oracles are hand-written by the authors; CASE's precision is high by construction. See README.

| condition | mean valid precision | mean valid recall | incompatible actionable (total) | unknown actionable (total) |
|---|---|---|---|---|
| SEMANTIC_ONLY | 0.50 | 1.00 | 16 | 5 |
| RECENCY_FILTERED | 0.43 | 0.43 | 6 | 5 |
| SEMANTIC_PLUS_RAW_CONFIG | 0.50 | 1.00 | 16 | 5 |
| CASE_VALIDITY_PACKAGE | 1.00 | 1.00 | 0 | 0 |
| CASE_NO_FLAG_EVIDENCE | 0.33 | 0.13 | 0 | 0 |
| CASE_NO_LINEAGE_EVIDENCE | 1.00 | 1.00 | 0 | 0 |

## Replay probe (same task and repository state, two deployments)

- t1-mig-us-new-signal: ACT_ON under us-prod = [code-aggregator, pr-214-graphql, test-graphql]; under eu-prod = [code-rest-controller, code-rest-service, pr-101-rest, test-rest]; prescriptive evidence changed: **true**
- t3-flag-eu-after-rollback: ACT_ON under eu-prod = [code-rest-controller, code-rest-service, pr-101-rest, test-rest]; under us-prod = [code-aggregator, pr-214-graphql, test-graphql]; prescriptive evidence changed: **true**
