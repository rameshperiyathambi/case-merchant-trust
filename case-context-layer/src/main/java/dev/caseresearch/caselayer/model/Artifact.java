package dev.caseresearch.caselayer.model;

import java.time.Instant;

/**
 * A candidate artifact (code, test, PR, ADR, ...).
 *
 * @param implementation runtime path from context/lineage.yaml, or "none" if expert-reviewed as deployment-independent
 * @param text           full file content (used for relevance only)
 */
public record Artifact(String id, String type, ArtifactRole role, String implementation, Instant updatedAt,
                       String path, String title, String summary, String text) {
}
