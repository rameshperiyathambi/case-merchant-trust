# Context package (CASE_VALIDITY_PACKAGE)

- Task: Add a new signal to the merchant trust score
- Target deployment: apac-prod
- Snapshot time t: 2026-06-01T00:00:00Z
- Budget: 1166/1500 tokens (approx.), dropped claims: 0

## ACT ON (valid for this deployment and time)

- [VALID] `history/prs/PR-101-rest-trust-score.md`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - PR-101 REST trust-score endpoint: How the REST trust score endpoint was built and how to add a new trust signal to it.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/main/java/dev/caseresearch/merchant/rest/LegacyTrustScoreService.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Legacy trust score formula service: Fixed weighted formula for the trust score from on-time shipping and dispute rate; add a new signal here.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag enabled for all traffic
- [VALID] `merchant-service/src/test/java/dev/caseresearch/merchant/LegacyTrustScoreServiceTest.java`
  - Treatment: Applicable: guard flag is on and no active supersession.
  - Legacy trust score formula tests: Tests for the REST trust score formula and signal boundaries.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.rest.enabled: flag enabled for all traffic

## VERIFY (unresolved; confirm before relying on it)

- [UNKNOWN] `merchant-service/src/main/java/dev/caseresearch/merchant/graphql/TrustSignalAggregator.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (trust-score.graphql.enabled: cohort-scoped flag ([beta-merchants], assignment unverified); deployment-wide status undefined)).
  - Trust signal aggregator and provider extension point: Add a new trust signal by registering a TrustSignalProvider bean; aggregator computes the weighted trust score.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: cohort-scoped flag ([beta-merchants], assignment unverified); deployment-wide status undefined
- [UNKNOWN] `merchant-service/src/test/java/dev/caseresearch/merchant/TrustSignalAggregatorTest.java`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (trust-score.graphql.enabled: cohort-scoped flag ([beta-merchants], assignment unverified); deployment-wide status undefined)).
  - Trust signal aggregator tests: Tests showing how a new trust signal plugs into the GraphQL aggregator.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: cohort-scoped flag ([beta-merchants], assignment unverified); deployment-wide status undefined
- [UNKNOWN] `history/prs/PR-214-graphql-migration.md`
  - Treatment: Verification obligation: confirm deployment-specific status before relying on this (guard flag status not established (trust-score.graphql.enabled: cohort-scoped flag ([beta-merchants], assignment unverified); deployment-wide status undefined)).
  - PR-214 GraphQL trust score with pluggable signals: GraphQL migration of the trust score; new trust signal is a new provider bean behind a flag.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.graphql.enabled: cohort-scoped flag ([beta-merchants], assignment unverified); deployment-wide status undefined

## EXPLAIN (historical rationale; NOT an implementation blueprint)

- [INVALID] `history/prs/PR-245-experimental-signal.md`
  - Treatment: Not applicable to apac-prod at t (guard flag is off for this deployment). Do not copy; explanation only.
  - PR-245 experimental return-rate trust signal: Most recent trust signal PR, a new trust signal behind an experiment flag.
  - Evidence: flags/apac-prod/2026-02-01.yaml @ 2026-02-01T00:00:00Z | scope=apac-prod | authority=AUTHORITATIVE | trust-score.experimental-signal.enabled: flag disabled for all traffic
- `history/adr/ADR-007-graphql.md`
  - Treatment: Historical rationale, not a blueprint.
  - ADR-007 REST to GraphQL decision: Decision record to move merchant read APIs including trust score from REST to GraphQL per deployment.
  - Evidence: context/artifacts.yaml @ 2025-12-01T00:00:00Z | scope=all deployments | authority=EXPERT_REVIEWED | no deployment-conditional preconditions recorded

