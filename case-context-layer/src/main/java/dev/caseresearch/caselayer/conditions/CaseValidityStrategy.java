package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.applicability.ApplicabilityEvaluator;
import dev.caseresearch.caselayer.graph.Ablation;
import dev.caseresearch.caselayer.model.Applicability;
import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.ArtifactRole;
import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.Judgment;
import dev.caseresearch.caselayer.model.Scored;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.Claim;
import dev.caseresearch.caselayer.packaging.ContextPackage;
import dev.caseresearch.caselayer.packaging.Packer;
import dev.caseresearch.caselayer.packaging.Section;
import dev.caseresearch.caselayer.packaging.SectionKind;

import java.util.ArrayList;
import java.util.List;

/**
 * CASE (paper Section III, Steps 2-3): adjudicates V(a | d, t) per candidate and packages
 * ACT_ON / EXPLAIN / VERIFY under the shared budget. Proven-incompatible candidates are withheld from
 * prescriptive use; unknowns carry a verification obligation. The ablation variants withhold one kind of evidence.
 */
public class CaseValidityStrategy implements ContextStrategy {

    private final ApplicabilityEvaluator evaluator;
    private final Condition condition;
    private final Ablation ablation;

    public CaseValidityStrategy(ApplicabilityEvaluator evaluator, Condition condition, Ablation ablation) {
        this.evaluator = evaluator;
        this.condition = condition;
        this.ablation = ablation;
    }

    public Condition condition() {
        return condition;
    }

    public ContextPackage build(TaskRequest req, List<Scored> pool) {
        List<Judgment> judgments = evaluator.evaluateAll(pool.stream().map(Scored::artifact).toList(),
                req.deployment(), req.asOf(), ablation);
        Packer p = new Packer(req.budgetTokens());
        List<String> notes = new ArrayList<>();

        // Priority: ACT_ON first, then VERIFY obligations, then EXPLAIN history.
        for (Judgment j : judgments) {
            if (j.artifact().role() == ArtifactRole.PRESCRIPTIVE && j.validity() == Applicability.VALID) {
                p.add(SectionKind.ACT_ON, claim(j, Applicability.VALID, "Applicable: " + j.rationale() + "."));
            }
        }
        for (Judgment j : judgments) {
            if (j.artifact().role() == ArtifactRole.PRESCRIPTIVE && j.validity() == Applicability.UNKNOWN) {
                p.add(SectionKind.VERIFY, claim(j, Applicability.UNKNOWN,
                        "Verification obligation: confirm deployment-specific status before relying on this ("
                                + j.rationale() + ")."));
            }
        }
        for (Judgment j : judgments) {
            if (j.artifact().role() == ArtifactRole.EXPLANATORY) {
                p.add(SectionKind.EXPLAIN, claim(j, null, "Historical rationale, not a blueprint."));
            } else if (j.validity() == Applicability.INVALID) {
                p.add(SectionKind.EXPLAIN, claim(j, Applicability.INVALID,
                        "Not applicable to " + req.deployment() + " at t (" + j.rationale()
                                + "). Do not copy; explanation only."));
            }
        }

        boolean hasValidPrescriptive = judgments.stream().anyMatch(
                j -> j.artifact().role() == ArtifactRole.PRESCRIPTIVE && j.validity() == Applicability.VALID);
        if (!hasValidPrescriptive) {
            notes.add("No valid prescriptive pattern is supported for " + req.deployment() + " at " + req.asOf()
                    + ". Request a deployment-specific check; do not invent a replacement architecture.");
        }
        return p.build(req, notes, SectionKind.ACT_ON, SectionKind.VERIFY, SectionKind.EXPLAIN);
    }

    private static Claim claim(Judgment j, Applicability status, String treatment) {
        Artifact a = j.artifact();
        return new Claim(a.id(), a.path(), status, treatment, a.title() + ": " + a.summary(), j.evidence());
    }
}
