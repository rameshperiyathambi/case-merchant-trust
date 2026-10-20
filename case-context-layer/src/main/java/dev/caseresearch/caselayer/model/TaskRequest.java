package dev.caseresearch.caselayer.model;

import java.time.Instant;

/** Task q, target deployment d, snapshot time t, condition, and token budget B. */
public record TaskRequest(String task, String deployment, Instant asOf, Condition condition, Integer budgetTokens,
                          Integer topK) {

    public TaskRequest {
        if (task == null || task.isBlank()) {
            throw new IllegalArgumentException("task is required");
        }
        if (deployment == null || deployment.isBlank()) {
            throw new IllegalArgumentException("deployment is required");
        }
        asOf = asOf == null ? Instant.now() : asOf;
        condition = condition == null ? Condition.CASE_VALIDITY_PACKAGE : condition;
        budgetTokens = budgetTokens == null ? 1500 : budgetTokens;
        topK = topK == null ? 8 : topK;
    }

    public TaskRequest withCondition(Condition c) {
        return new TaskRequest(task, deployment, asOf, c, budgetTokens, topK);
    }

    public TaskRequest withDeployment(String d) {
        return new TaskRequest(task, d, asOf, condition, budgetTokens, topK);
    }
}
