# Context package (CASE_NO_LINEAGE_EVIDENCE)

- Task: Add a new weighted signal to the merchant trust score
- Target deployment: eu-prod
- Snapshot time t: 2026-03-01T00:00:00Z
- Budget: 994/1500 tokens (approx.), dropped claims: 0

## ACT ON (valid for this deployment and time)

- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `history/prs/PR-214-graphql-migration.md`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/MerchantTrustResolver.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - GraphQL merchant trust resolver: GraphQL resolver exposing merchant trust score and signals.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- [INVALID] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Not applicable to eu-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Not applicable to eu-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
- [INVALID] `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Treatment: Not applicable to eu-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
  - Evidence: flags/eu-prod/2026-01-20.yaml @ 2026-01-20T00:00:00Z | scope=eu-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
- `history/adr/ADR-007-graphql.md`
  - Treatment: Historical rationale, not a blueprint.
  - ADR-007 REST to GraphQL decision: Decision record to move merchant read APIs including trust score from REST to GraphQL per deployment.
  - Evidence: context/artifacts.yaml @ 2025-12-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded

