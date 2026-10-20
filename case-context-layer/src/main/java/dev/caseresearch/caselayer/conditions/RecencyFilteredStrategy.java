package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.Scored;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.Claim;
import dev.caseresearch.caselayer.packaging.ContextPackage;
import dev.caseresearch.caselayer.packaging.Packer;
import dev.caseresearch.caselayer.packaging.SectionKind;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

/** Baseline 2: drop pool artifacts older than 180 days before t; newest first. */
@Component
public class RecencyFilteredStrategy implements ContextStrategy {

    static final Duration WINDOW = Duration.ofDays(180);

    public Condition condition() {
        return Condition.RECENCY_FILTERED;
    }

    public ContextPackage build(TaskRequest req, List<Scored> pool) {
        Packer p = new Packer(req.budgetTokens());
        pool.stream().map(Scored::artifact)
                .filter(a -> a.updatedAt().isAfter(req.asOf().minus(WINDOW)))
                .sorted(Comparator.comparing(Artifact::updatedAt).reversed())
                .forEach(a -> p.add(SectionKind.CONTEXT, Claim.plain(a)));
        return p.build(req, List.of(), SectionKind.CONTEXT);
    }
}
