# Context package (SEMANTIC_ONLY)

- Task: Add lookup by region to the merchant profile repository
- Target deployment: apac-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 490/1500 tokens (approx.), dropped claims: 0

## RETRIEVED CONTEXT (unadjudicated)

- `merchant-service/src/test/java/dev/caseresearch/merchant/MerchantRepositoryTest.java`
  - Merchant repository tests (stable): Tests for merchant lookup and profile repository.
- `merchant-service/src/main/java/dev/caseresearch/merchant/MerchantRepository.java`
  - Merchant repository (stable): Stable in-memory merchant profile repository for merchant lookup and profile fields; same in every deployment.
- `history/prs/PR-101-rest-trust-score.md`
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
- `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
- `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreController.java`
  - Legacy REST trust-score controller: REST endpoint GET /api/v1/merchants/id/trust-score; extend trust score signal by changing LegacyTrustScoreService formula.
- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.
- `history/migration-note-rest-to-graphql.md`
  - REST to GraphQL migration note: Migration order and guidance for teams extending the trust score; check flags first.

