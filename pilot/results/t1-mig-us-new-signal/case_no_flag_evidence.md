# Context package (CASE_NO_FLAG_EVIDENCE)

- Task: Extend the merchant trust score with a new return rate signal
- Target deployment: us-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 977/1500 tokens (approx.), dropped claims: 0

> No valid prescriptive pattern is supported for us-prod at 2026-06-01T00:00:00Z. Request a deployment-specific check; do not invent a replacement architecture.

## VERIFY (unresolved; confirm before relying on it)

- [UNKNOWN] `history/prs/PR-214-graphql-migration.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
- [UNKNOWN] `history/prs/PR-245-experimental-signal.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - PR-245 experimental return-rate trust signal: Most recent trust signal PR, a new trust signal behind an experiment flag.
- [UNKNOWN] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- [INVALID] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Not applicable to us-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Not applicable to us-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod
- [INVALID] `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Treatment: Not applicable to us-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreController.java`
  - Treatment: Not applicable to us-prod at t (superseded in this deployment; flag state not established). Do not copy; explanation only.
  - Legacy REST trust-score controller: REST endpoint GET /api/v1/merchants/id/trust-score; extend trust score signal by changing LegacyTrustScoreService formula.
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod

