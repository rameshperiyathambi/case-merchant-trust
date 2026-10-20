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

import dev.caseresearch.flags.FlagRepository;

/**
 * Baseline 3: semantic retrieval plus the raw flag snapshot for (d, t), unadjudicated. Controls for the
 * possibility that more runtime information, not structured adjudication, explains improvements.
 */
@Component
public class SemanticPlusRawConfigStrategy implements ContextStrategy {

    private final FlagRepository flags;

    public SemanticPlusRawConfigStrategy(FlagRepository flags) {
        this.flags = flags;
    }

    public Condition condition() {
        return Condition.SEMANTIC_PLUS_RAW_CONFIG;
    }

    public ContextPackage build(TaskRequest req, List<Scored> pool) {
        Packer p = new Packer(req.budgetTokens());
        flags.asOf(req.deployment(), req.asOf()).ifPresent(s -> p.add(SectionKind.CONFIG,
                new Claim("flags:" + s.deployment(), s.source(), null, null, "```yaml\n" + s.rawText().strip() + "\n```",
                        List.of())));
        for (Scored s : pool) {
            p.add(SectionKind.CONTEXT, Claim.plain(s.artifact()));
        }
        return p.build(req, List.of(), SectionKind.CONFIG, SectionKind.CONTEXT);
    }
}
