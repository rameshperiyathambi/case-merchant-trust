# PR-231: Roll back eu-prod to REST trust score

Merged: 2026-04-02 | Author: synthetic-dev-c

Latency regression in eu-prod caused by GraphQL resolver fan-out. The deployment flags are reverted:
`trust-score.graphql.enabled=false`, `trust-score.rest.enabled=true`. Other regions are unchanged.

GraphQL remains the target architecture. EU re-enable is tracked separately and has no date.
