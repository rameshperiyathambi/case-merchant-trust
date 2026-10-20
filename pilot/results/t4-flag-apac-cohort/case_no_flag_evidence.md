# Context package (CASE_NO_FLAG_EVIDENCE)

- Task: Add a new signal to the merchant trust score
- Target deployment: apac-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 822/1500 tokens (approx.), dropped claims: 0

> No valid prescriptive pattern is supported for apac-prod at 2026-06-01T00:00:00Z. Request a deployment-specific check; do not invent a replacement architecture.

## VERIFY (unresolved; confirm before relying on it)

- [UNKNOWN] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
- [UNKNOWN] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
- [UNKNOWN] `history/prs/PR-214-graphql-migration.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
- [UNKNOWN] `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
- [UNKNOWN] `history/prs/PR-245-experimental-signal.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - PR-245 experimental return-rate trust signal: Most recent trust signal PR, a new trust signal behind an experiment flag.

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- `history/adr/ADR-007-graphql.md`
  - Treatment: Historical rationale, not a blueprint.
  - ADR-007 REST to GraphQL decision: Decision record to move merchant read APIs including trust score from REST to GraphQL per deployment.
  - Evidence: context/artifacts.yaml @ 2025-12-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded

