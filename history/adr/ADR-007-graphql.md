# ADR-007: Move merchant read APIs from REST to GraphQL

Status: accepted (2025-12-01) | Decision: new trust-score work targets GraphQL.
The REST v1 trust-score endpoint is superseded per deployment as GraphQL is enabled (see `context/lineage.yaml`).
Consequence: per-deployment migration, not a global cutover; legacy REST stays until a deployment is migrated.
