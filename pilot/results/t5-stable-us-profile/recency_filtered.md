# Context package (RECENCY_FILTERED)

- Task: Add lookup by name to the merchant profile repository
- Target deployment: us-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 226/1500 tokens (approx.), dropped claims: 0

## RETRIEVED CONTEXT (unadjudicated)

- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.
- `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.

