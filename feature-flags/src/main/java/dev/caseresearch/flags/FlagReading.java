package dev.caseresearch.flags;

import java.time.Instant;

/** Deployment-level reading of a flag, with provenance. */
public record FlagReading(String flag, String deployment, Tri state, String source, Instant evidenceAt,
                          String authority, String reason) {
}
