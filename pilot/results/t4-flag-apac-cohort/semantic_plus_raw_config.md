# Context package (SEMANTIC_PLUS_RAW_CONFIG)

- Task: Add a new signal to the merchant trust score
- Target deployment: apac-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 633/1500 tokens (approx.), dropped claims: 0

## RAW CONFIG (unadjudicated)

- `flags/apac-prod/2026-02-01.yaml`
  - ```yaml
    # Synthetic flag export. All data is fictional.
    deployment: apac-prod
    effectiveAt: "2026-02-01T00:00:00Z"
    authority: AUTHORITATIVE
    flags:
      trust-score.graphql.enabled:
        enabled: true
        scope: COHORT
        cohorts: [beta-merchants]
        assignmentVerified: false
      trust-score.rest.enabled:
        enabled: true
        scope: ALL
      trust-score.rest-fallback.enabled:
        enabled: true
        scope: COHORT
        cohorts: [beta-merchants]
        assignmentVerified: false
      trust-score.experimental-signal.enabled:
        enabled: false
        scope: ALL
    ```

## RETRIEVED CONTEXT (unadjudicated)

- `history/prs/PR-101-rest-trust-score.md`
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
- `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
- `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
- `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
- `history/prs/PR-214-graphql-migration.md`
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
- `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
- `history/prs/PR-245-experimental-signal.md`
  - PR-245 experimental return-rate trust signal: Most recent trust signal PR, a new trust signal behind an experiment flag.
- `history/adr/ADR-007-graphql.md`
  - ADR-007 REST to GraphQL decision: Decision record to move merchant read APIs including trust score from REST to GraphQL per deployment.

