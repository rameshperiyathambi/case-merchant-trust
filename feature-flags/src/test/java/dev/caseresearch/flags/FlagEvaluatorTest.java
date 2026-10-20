package dev.caseresearch.flags;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class FlagEvaluatorTest {

    private static final Instant T = Instant.parse("2026-06-01T00:00:00Z");
    private final FlagRepository repo = new FlagRepository("");
    private final FlagEvaluator eval = new FlagEvaluator(repo);

    @Test
    void deploymentWideFlagsAreTrueOrFalse() {
        assertThat(eval.reading("us-prod", "trust-score.graphql.enabled", T).state()).isEqualTo(Tri.TRUE);
        assertThat(eval.reading("us-prod", "trust-score.rest.enabled", T).state()).isEqualTo(Tri.FALSE);
    }

    @Test
    void cohortScopedFlagIsUnknownAtDeploymentLevel() {
        FlagReading r = eval.reading("apac-prod", "trust-score.graphql.enabled", T);
        assertThat(r.state()).isEqualTo(Tri.UNKNOWN);
        assertThat(r.reason()).contains("cohort-scoped");
    }

    @Test
    void missingSnapshotOrFlagIsUnknown() {
        assertThat(eval.reading("nowhere", "trust-score.rest.enabled", T).state()).isEqualTo(Tri.UNKNOWN);
        assertThat(eval.reading("us-prod", "no.such.flag", T).state()).isEqualTo(Tri.UNKNOWN);
        assertThat(eval.reading("apac-prod", "trust-score.rest.enabled", Instant.parse("2025-01-01T00:00:00Z")).state())
                .isEqualTo(Tri.UNKNOWN);
    }

    @Test
    void runtimeDecisionHonoursCohort() {
        assertThat(eval.enabledFor("apac-prod", "trust-score.graphql.enabled", "beta-merchants", T)).isTrue();
        assertThat(eval.enabledFor("apac-prod", "trust-score.graphql.enabled", "default", T)).isFalse();
        assertThat(eval.enabledFor("us-prod", "trust-score.graphql.enabled", "default", T)).isTrue();
    }
}
