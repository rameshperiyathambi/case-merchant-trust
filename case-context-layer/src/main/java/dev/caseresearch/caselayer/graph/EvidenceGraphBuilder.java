package dev.caseresearch.caselayer.graph;

import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.Evidence;
import dev.caseresearch.caselayer.retrieval.ArtifactCatalog;

import java.time.Instant;

import org.springframework.stereotype.Component;

import dev.caseresearch.flags.FlagEvaluator;
import dev.caseresearch.flags.FlagReading;
import dev.caseresearch.flags.Tri;

/** Links artifacts to implementations, flags, deployments and supersession records as of (d, t). */
@Component
public class EvidenceGraphBuilder {

    private final ArtifactCatalog catalog;
    private final Lineage lineage;
    private final FlagEvaluator flags;

    public EvidenceGraphBuilder(ArtifactCatalog catalog, Lineage lineage, FlagEvaluator flags) {
        this.catalog = catalog;
        this.lineage = lineage;
        this.flags = flags;
    }

    public EvidenceGraph build(String deployment, Instant t, Ablation ablation) {
        EvidenceGraph g = new EvidenceGraph();
        for (Artifact a : catalog.all()) {
            String impl = "impl:" + a.implementation();
            g.add(new EvidenceGraph.Edge("artifact:" + a.id(), "IMPLEMENTS", impl, Tri.TRUE,
                    new Evidence("context/artifacts.yaml", a.updatedAt(), deployment, "EXPERT_REVIEWED",
                            a.id() + " implements " + a.implementation())));
        }
        catalog.all().stream().map(Artifact::implementation).distinct().forEach(impl -> {
            if (ablation != Ablation.NO_FLAG_EVIDENCE) lineage.requiredFlag(impl).ifPresent(flag -> {
                FlagReading r = flags.reading(deployment, flag, t);
                Evidence ev = new Evidence(r.source() == null ? "flags/" + deployment : r.source(), r.evidenceAt(),
                        deployment, r.authority(), flag + ": " + r.reason());
                g.add(new EvidenceGraph.Edge("flag:" + flag, "GUARDS", "impl:" + impl, r.state(), ev));
                if (r.state() == Tri.TRUE) {
                    g.add(new EvidenceGraph.Edge("impl:" + impl, "DEPLOYED_IN", "deployment:" + deployment,
                            Tri.TRUE, ev));
                }
            });
            if (ablation != Ablation.NO_LINEAGE_EVIDENCE) lineage.finding(impl, deployment, t).ifPresent(f -> g.add(new EvidenceGraph.Edge(
                    "impl:" + f.supersededBy(), "SUPERSEDES", "impl:" + impl,
                    f.active() ? Tri.TRUE : Tri.FALSE, f.evidence())));
        });
        return g;
    }
}
