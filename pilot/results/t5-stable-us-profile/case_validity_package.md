# Context package (CASE_VALIDITY_PACKAGE)

- Task: Add lookup by name to the merchant profile repository
- Target deployment: us-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 1141/1500 tokens (approx.), dropped claims: 0

## ACT ON (valid for this deployment and time)

- [VALID] `merchant-service/src/test/java/dev/caseresearch/merchant/MerchantRepositoryTest.java`
  - Treatment: Applicable: deployment-independent.
  - Merchant repository tests (stable): Tests for merchant lookup and profile repository.
  - Evidence: context/artifacts.yaml @ 2025-03-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/MerchantRepository.java`
  - Treatment: Applicable: deployment-independent.
  - Merchant repository (stable): Stable in-memory merchant profile repository for merchant lookup and profile fields; same in every deployment.
  - Evidence: context/artifacts.yaml @ 2025-03-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- [INVALID] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreController.java`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy REST trust-score controller: REST endpoint GET /api/v1/merchants/id/trust-score; extend trust score signal by changing LegacyTrustScoreService formula.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
  - Evidence: history/adr/ADR-007-graphql.md @ 2026-01-15T00:00:00Z | scope=us-prod | authority=LINEAGE | rest-trust-score superseded by graphql-trust-score in us-prod

