package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.Scored;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.Claim;
import dev.caseresearch.caselayer.packaging.ContextPackage;
import dev.caseresearch.caselayer.packaging.Packer;
import dev.caseresearch.caselayer.packaging.SectionKind;

import java.util.List;

import org.springframework.stereotype.Component;

/** Baseline 1: similarity-ranked artifacts, no applicability reasoning. */
@Component
public class SemanticOnlyStrategy implements ContextStrategy {

    public Condition condition() {
        return Condition.SEMANTIC_ONLY;
    }

    public ContextPackage build(TaskRequest req, List<Scored> pool) {
        Packer p = new Packer(req.budgetTokens());
        for (Scored s : pool) {
            p.add(SectionKind.CONTEXT, Claim.plain(s.artifact()));
        }
        return p.build(req, List.of(), SectionKind.CONTEXT);
    }
}
