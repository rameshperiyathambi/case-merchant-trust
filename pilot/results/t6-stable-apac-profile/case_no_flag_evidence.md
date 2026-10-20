# Context package (CASE_NO_FLAG_EVIDENCE)

- Task: Add lookup by region to the merchant profile repository
- Target deployment: apac-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 870/1500 tokens (approx.), dropped claims: 0

## ACT ON (valid for this deployment and time)

- [VALID] `merchant-service/src/test/java/dev/caseresearch/merchant/MerchantRepositoryTest.java`
  - Treatment: Applicable: deployment-independent.
  - Merchant repository tests (stable): Tests for merchant lookup and profile repository.
  - Evidence: context/artifacts.yaml @ 2025-03-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/MerchantRepository.java`
  - Treatment: Applicable: deployment-independent.
  - Merchant repository (stable): Stable in-memory merchant profile repository for merchant lookup and profile fields; same in every deployment.
  - Evidence: context/artifacts.yaml @ 2025-03-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded

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
- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreController.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - Legacy REST trust-score controller: REST endpoint GET /api/v1/merchants/id/trust-score; extend trust score signal by changing LegacyTrustScoreService formula.
- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (no flag evidence available)).
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- `history/migration-note-rest-to-graphql.md`
  - Treatment: Historical rationale, not a blueprint.
  - REST to GraphQL migration note: Migration order and guidance for teams extending the trust score; check flags first.
  - Evidence: context/artifacts.yaml @ 2025-12-15T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded

