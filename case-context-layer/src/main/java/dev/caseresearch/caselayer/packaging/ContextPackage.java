package dev.caseresearch.caselayer.packaging;

import dev.caseresearch.caselayer.model.Condition;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Bounded, source-backed context package P_B(q, d, t) handed to an unmodified coding agent (paper Section III, Step 3). */
public record ContextPackage(Condition condition, String task, String deployment, Instant asOf, int budgetTokens,
                             int usedTokens, int droppedClaims, List<Section> sections, List<String> notes) {

    /** Artifacts presented to the agent as something it may act on / copy from. */
    public Set<String> actionableIds() {
        return sections.stream()
                .filter(s -> s.kind() == SectionKind.ACT_ON || s.kind() == SectionKind.CONTEXT)
                .flatMap(s -> s.claims().stream()).map(Claim::artifactId).collect(Collectors.toCollection(java.util.TreeSet::new));
    }

    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Context package (").append(condition).append(")\n\n");
        sb.append("- Task: ").append(task).append('\n');
        sb.append("- Target deployment: ").append(deployment).append('\n');
        sb.append("- Snapshot time t: ").append(asOf).append('\n');
        sb.append("- Budget: ").append(usedTokens).append('/').append(budgetTokens).append(" tokens (approx.), dropped claims: ")
                .append(droppedClaims).append("\n\n");
        for (String n : notes) {
            sb.append("> ").append(n).append("\n\n");
        }
        for (Section s : sections) {
            sb.append("## ").append(title(s.kind())).append("\n\n");
            s.claims().forEach(c -> sb.append(c.render()));
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String title(SectionKind k) {
        return switch (k) {
            case ACT_ON -> "ACT ON (valid for this deployment and time)";
            case EXPLAIN -> "EXPLAIN (historical rationale; NOT an implementation blueprint)";
            case VERIFY -> "VERIFY (unresolved; confirm before relying on it)";
            case CONFIG -> "RAW CONFIG (unadjudicated)";
            case CONTEXT -> "RETRIEVED CONTEXT (unadjudicated)";
        };
    }
}
