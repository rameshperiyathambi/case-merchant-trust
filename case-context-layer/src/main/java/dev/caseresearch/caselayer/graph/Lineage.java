package dev.caseresearch.caselayer.graph;

import dev.caseresearch.caselayer.model.Evidence;
import dev.caseresearch.caselayer.packaging.Section;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.caseresearch.flags.RepoPaths;
import dev.caseresearch.flags.YamlFiles;

/**
 * Implementation preconditions and per-deployment supersession records ({@code context/lineage.yaml}).
 * Paper Section III, Step 1: the "supersedes" and "guards" evidence.
 */
@Component
public class Lineage {

    record Implementation(String requiresFlag) {
    }

    record DeploymentSupersession(Instant effectiveAt, Instant revertedAt, String revertSource) {
    }

    record Supersession(String implementation, String supersededBy, String source,
                        Map<String, DeploymentSupersession> deployments) {
    }

    private record LineageFile(Map<String, Implementation> implementations, List<Supersession> supersessions) {
    }

    /** Finding about a supersession edge; {@code active=false} means it was reverted before t. */
    public record Finding(boolean active, String supersededBy, Evidence evidence) {
    }

    private final LineageFile file;

    public Lineage(@Value("${case.repo-root:}") String configuredRoot) {
        this.file = YamlFiles.read(RepoPaths.repoRoot(configuredRoot).resolve("context/lineage.yaml"),
                LineageFile.class);
    }

    public Optional<String> requiredFlag(String implementation) {
        return Optional.ofNullable(file.implementations().get(implementation)).map(Implementation::requiresFlag);
    }

    public Optional<Finding> finding(String implementation, String deployment, Instant t) {
        for (Supersession s : file.supersessions()) {
            DeploymentSupersession d = s.deployments().get(deployment);
            if (!implementation.equals(s.implementation()) || d == null || d.effectiveAt().isAfter(t)) {
                continue;
            }
            if (d.revertedAt() != null && !d.revertedAt().isAfter(t)) {
                return Optional.of(new Finding(false, s.supersededBy(), new Evidence(d.revertSource(), d.revertedAt(),
                        deployment, "LINEAGE", implementation + " supersession by " + s.supersededBy()
                        + " was reverted in " + deployment)));
            }
            return Optional.of(new Finding(true, s.supersededBy(), new Evidence(s.source(), d.effectiveAt(),
                    deployment, "LINEAGE", implementation + " superseded by " + s.supersededBy() + " in " + deployment)));
        }
        return Optional.empty();
    }
}
