package dev.caseresearch.caselayer.model;

import java.time.Instant;

/** One applicability assertion's provenance: source, timestamp, scope and authority. */
public record Evidence(String source, Instant timestamp, String scope, String authority, String statement) {
}
