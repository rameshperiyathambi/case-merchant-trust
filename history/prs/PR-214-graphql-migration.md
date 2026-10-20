# PR-214: Serve trust score through GraphQL with pluggable signals

Merged: 2026-01-12 | Author: synthetic-dev-b

Introduces `merchant.graphqls`, `MerchantTrustResolver`, and `TrustSignalAggregator`.
Signals are `TrustSignalProvider` beans, so a new signal is a new bean; no schema or aggregator change is needed
unless the signal needs a new field.

## Rollout
Guarded by `trust-score.graphql.enabled`. When on, `trust-score.rest.enabled` is turned off in the same release.
First enabled: us-prod 2026-01-15, eu-prod 2026-01-20. APAC pending cohort rollout.

## Tests
`TrustSignalAggregatorTest`.
