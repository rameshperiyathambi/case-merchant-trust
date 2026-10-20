package dev.caseresearch.caselayer.applicability;

import dev.caseresearch.caselayer.graph.Ablation;
import dev.caseresearch.caselayer.graph.EvidenceGraph;
import dev.caseresearch.caselayer.graph.EvidenceGraphBuilder;
import dev.caseresearch.caselayer.model.Applicability;
import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.Evidence;
import dev.caseresearch.caselayer.model.Judgment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import dev.caseresearch.flags.Tri;

/**
 * Computes V(a | d, t) (paper Eq. 1) from the evidence graph.
 * <ul>
 *   <li>guard flag authoritatively FALSE: INVALID</li>
 *   <li>guard flag TRUE and no active supersession: VALID; TRUE but supersession active: conflict, UNKNOWN</li>
 *   <li>guard flag UNKNOWN (cohort-scoped, missing, non-authoritative): INVALID only if a supersession is active,
 *       else UNKNOWN</li>
 *   <li>absence of runtime traces is never evidence of invalidity (no such input exists)</li>
 * </ul>
 */
@Component
public class ApplicabilityEvaluator {

    private final EvidenceGraphBuilder graphs;

    public ApplicabilityEvaluator(EvidenceGraphBuilder graphs) {
        this.graphs = graphs;
    }

    public List<Judgment> evaluateAll(List<Artifact> artifacts, String deployment, Instant t, Ablation ablation) {
        EvidenceGraph g = graphs.build(deployment, t, ablation);
        return artifacts.stream().map(a -> evaluate(a, g)).toList();
    }

    public Judgment evaluate(Artifact a, String deployment, Instant t) {
        return evaluate(a, graphs.build(deployment, t, Ablation.NONE));
    }

    private Judgment evaluate(Artifact a, EvidenceGraph g) {
        if ("none".equals(a.implementation())) {
            return new Judgment(a, Applicability.VALID, List.of(new Evidence("context/artifacts.yaml", a.updatedAt(),
                    "all deployments", "EXPERT_REVIEWED", "no deployment-conditional preconditions recorded")),
                    "deployment-independent");
        }
        String impl = "impl:" + a.implementation();
        List<EvidenceGraph.Edge> guards = g.edgesTo(impl, "GUARDS");
        List<EvidenceGraph.Edge> supersessions = g.edgesTo(impl, "SUPERSEDES");
        List<Evidence> evidence = new ArrayList<>();
        guards.forEach(e -> evidence.add(e.evidence()));
        supersessions.forEach(e -> evidence.add(e.evidence()));

        Tri flag = guards.isEmpty() ? Tri.UNKNOWN : guards.get(0).state();
        boolean supersededActive = supersessions.stream().anyMatch(e -> e.state() == Tri.TRUE);
        if (flag == Tri.FALSE) {
            return new Judgment(a, Applicability.INVALID, evidence, "guard flag is off for this deployment");
        }
        if (flag == Tri.TRUE) {
            return supersededActive
                    ? new Judgment(a, Applicability.UNKNOWN, evidence,
                            "conflict: guard flag is on but a supersession record is active")
                    : new Judgment(a, Applicability.VALID, evidence, "guard flag is on and no active supersession");
        }
        if (supersededActive) {
            return new Judgment(a, Applicability.INVALID, evidence,
                    "superseded in this deployment; flag state not established");
        }
        String detail = guards.isEmpty() ? "no flag evidence available" : guards.get(0).evidence().statement();
        return new Judgment(a, Applicability.UNKNOWN, evidence, "guard flag status not established (" + detail + ")");
    }
}
