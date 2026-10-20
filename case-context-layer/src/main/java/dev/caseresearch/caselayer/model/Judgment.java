package dev.caseresearch.caselayer.model;

import java.util.List;

public record Judgment(Artifact artifact, Applicability validity, List<Evidence> evidence, String rationale) {
}
