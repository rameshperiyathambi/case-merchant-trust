package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.Scored;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.ContextPackage;

import java.util.List;

public interface ContextStrategy {
    Condition condition();

    /** @param pool shared candidate pool, already restricted to artifacts available at time t */
    ContextPackage build(TaskRequest req, List<Scored> pool);
}
