# Context package (SEMANTIC_PLUS_RAW_CONFIG)

- Task: Add lookup by name to the merchant profile repository
- Target deployment: us-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 631/1500 tokens (approx.), dropped claims: 0

## RAW CONFIG (unadjudicated)

- `flags/us-prod/2026-01-15.yaml`
  - ```yaml
    # Synthetic flag export. All data is fictional.
    deployment: us-prod
    effectiveAt: "2026-01-15T00:00:00Z"
    authority: AUTHORITATIVE
    flags:
      trust-score.graphql.enabled:
        enabled: true
        scope: ALL
      trust-score.rest.enabled:
        enabled: false
        scope: ALL
      trust-score.rest-fallback.enabled:
        enabled: false
        scope: ALL
      trust-score.experimental-signal.enabled:
        enabled: false
        scope: ALL
    ```

## RETRIEVED CONTEXT (unadjudicated)

- `merchant-service/src/test/java/dev/caseresearch/merchant/MerchantRepositoryTest.java`
  - Merchant repository tests (stable): Tests for merchant lookup and profile repository.
- `merchant-service/src/main/java/dev/caseresearch/merchant/MerchantRepository.java`
  - Merchant repository (stable): Stable in-memory merchant profile repository for merchant lookup and profile fields; same in every deployment.
- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
- `history/prs/PR-101-rest-trust-score.md`
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.
- `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
- `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
- `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreController.java`
  - Legacy REST trust-score controller: REST endpoint GET /api/v1/merchants/id/trust-score; extend trust score signal by changing LegacyTrustScoreService formula.

