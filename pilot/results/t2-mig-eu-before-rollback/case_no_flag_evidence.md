# Context package (CASE_NO_FLAG_EVIDENCE)

- Task: Add a new weighted signal to the merchant trust score
- Target deployment: eu-prod
- Snapshot time t: 2026-03-01T00:00:00Z
- Budget: 935/1500 tokens (approx.), dropped claims: 0

> No valid prescriptive pattern is supported for eu-prod at 2026-03-01T00:00:00Z. Request a deployment-specific check; do not invent a replacement architecture.

## VERIFY (unresolved; confirm before relying on it)

- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
- [UNKNOWN] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
- [UNKNOWN] `history/prs/PR-214-graphql-migration.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- [INVALID] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Not applicable to eu-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in eu-prod
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Not applicable to eu-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in eu-prod
- [INVALID] `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Treatment: Not applicable to eu-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in eu-prod
- `history/adr/ADR-007-graphql.md`
  - Treatment: Historical rationale, not a blueprint.
  - ADR-007 REST to GraphQL decision: Decision record to move merchant read APIs including trust score from REST to GraphQL per deployment.
  - Evidence: context/artifacts.yaml @ 2025-12-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded

