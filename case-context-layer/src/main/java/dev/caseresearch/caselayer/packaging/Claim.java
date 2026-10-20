package dev.caseresearch.caselayer.packaging;

import dev.caseresearch.caselayer.model.Applicability;
import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.Evidence;

import java.util.List;

/** An actionable or explanatory claim with pointer, status (null = not adjudicated) and provenance. */
public record Claim(String artifactId, String pointer, Applicability status, String treatment, String snippet,
                    List<Evidence> evidence) {

    /** An unadjudicated claim used by the baseline conditions. */
    public static Claim plain(Artifact a) {
        return new Claim(a.id(), a.path(), null, null, a.title() + ": " + a.summary(), List.of());
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("- ").append(status == null ? "" : "[" + status + "] ").append('`').append(pointer).append("`\n");
        if (treatment != null && !treatment.isBlank()) {
            sb.append("  - Treatment: ").append(treatment).append('\n');
        }
        sb.append("  - ").append(snippet.replace("\n", "\n    ")).append('\n');
        for (Evidence e : evidence) {
            sb.append("  - Evidence: ").append(e.source()).append(e.timestamp() == null ? "" : " @ " + e.timestamp())
                    .append(" | scope=").append(e.scope())
                    .append(e.authority() == null ? "" : " | authority=" + e.authority())
                    .append(" | ").append(e.statement()).append('\n');
        }
        return sb.toString();
    }
}
