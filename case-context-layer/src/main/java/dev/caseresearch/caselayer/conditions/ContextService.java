package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.Scored;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.ContextPackage;
import dev.caseresearch.caselayer.retrieval.Bm25Retriever;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/** Retrieves one shared candidate pool and applies the requested condition's strategy. */
@Service
public class ContextService {

    private final Bm25Retriever retriever;
    private final Map<Condition, ContextStrategy> strategies = new EnumMap<>(Condition.class);

    public ContextService(Bm25Retriever retriever, List<ContextStrategy> all) {
        this.retriever = retriever;
        all.forEach(s -> strategies.put(s.condition(), s));
    }

    public ContextPackage build(TaskRequest req, List<Scored> pool) {
        return strategies.get(req.condition()).build(req, pool);
    }

    /** Pool shared across conditions; only artifacts that existed by t may enter the context. */
    public List<Scored> pool(TaskRequest req) {
        return retriever.topK(req.task(), 1000).stream()
                .filter(s -> !s.artifact().updatedAt().isAfter(req.asOf()))
                .limit(req.topK()).toList();
    }

    public ContextPackage build(TaskRequest req) {
        return strategies.get(req.condition()).build(req, pool(req));
    }
}
