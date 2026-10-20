# Context package (CASE_NO_LINEAGE_EVIDENCE)

- Task: Extend the merchant trust score with a new return rate signal
- Target deployment: us-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 1045/1500 tokens (approx.), dropped claims: 0

## ACT ON (valid for this deployment and time)

- [VALID] `history/prs/PR-214-graphql-migration.md`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: flag enabled for all traffic

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- [INVALID] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
- [INVALID] `history/prs/PR-245-experimental-signal.md`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - PR-245 experimental return-rate trust signal: Most recent trust signal PR, a new trust signal behind an experiment flag.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.experimental-signal.enabled: flag disabled for all traffic
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
- [INVALID] `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic
- [INVALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreController.java`
  - Treatment: Not applicable to us-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - Legacy REST trust-score controller: REST endpoint GET /api/v1/merchants/id/trust-score; extend trust score signal by changing LegacyTrustScoreService formula.
  - Evidence: flags/us-prod/2026-01-15.yaml @ 2026-01-15T00:00:00Z | scope=us-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag disabled for all traffic

