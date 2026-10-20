# PR-101: Add REST trust-score endpoint for merchants

Merged: 2024-11-10 | Author: synthetic-dev-a

Adds `GET /api/v1/merchants/{id}/trust-score` backed by `LegacyTrustScoreService`.
The score is a fixed weighted formula over on-time shipping rate and dispute rate.

## Design notes
- Controller: `LegacyTrustScoreController`; formula: `LegacyTrustScoreService`.
- To add a new signal, extend the formula in `LegacyTrustScoreService` and add a field to `LegacyTrustScore`.
- Tests: `LegacyTrustScoreServiceTest` covers the formula boundaries.

## Rollout
Enabled for all traffic via `trust-score.rest.enabled`.
