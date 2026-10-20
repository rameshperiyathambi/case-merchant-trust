package dev.caseresearch.caselayer.applicability;

import dev.caseresearch.caselayer.model.Applicability;
import dev.caseresearch.caselayer.model.Judgment;
import dev.caseresearch.caselayer.retrieval.ArtifactCatalog;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicabilityEvaluatorTest {

    private static final Instant T = Instant.parse("2026-06-01T00:00:00Z");

    @Autowired
    ApplicabilityEvaluator evaluator;
    @Autowired
    ArtifactCatalog catalog;

    private Applicability v(String artifactId, String deployment, Instant t) {
        return evaluator.evaluate(catalog.byId(artifactId).orElseThrow(), deployment, t).validity();
    }

    @Test
    void migrationCase_restInvalid_graphqlValid() {
        assertThat(v("code-rest-controller", "us-prod", T)).isEqualTo(Applicability.INVALID);
        assertThat(v("code-aggregator", "us-prod", T)).isEqualTo(Applicability.VALID);
    }

    @Test
    void regionalRollback_sameTimeDifferentDeployments() {
        assertThat(v("code-rest-controller", "us-prod", T)).isEqualTo(Applicability.INVALID);
        assertThat(v("code-rest-controller", "eu-prod", T)).isEqualTo(Applicability.VALID);
        assertThat(v("code-aggregator", "eu-prod", T)).isEqualTo(Applicability.INVALID);
    }

    @Test
    void sameDeploymentChangesOverTime() {
        Instant beforeRollback = Instant.parse("2026-03-01T00:00:00Z");
        assertThat(v("code-rest-controller", "eu-prod", beforeRollback)).isEqualTo(Applicability.INVALID);
        assertThat(v("code-aggregator", "eu-prod", beforeRollback)).isEqualTo(Applicability.VALID);
    }

    @Test
    void cohortScopedUnverifiedFlag_isUnknownNotInvalid() {
        assertThat(v("code-aggregator", "apac-prod", T)).isEqualTo(Applicability.UNKNOWN);
        assertThat(v("code-fallback", "apac-prod", T)).isEqualTo(Applicability.UNKNOWN);
    }

    @Test
    void missingEvidenceIsUnknown_notInvalid() {
        // Before any snapshot exists for apac-prod there is no evidence either way.
        assertThat(v("code-aggregator", "apac-prod", Instant.parse("2025-01-01T00:00:00Z")))
                .isEqualTo(Applicability.UNKNOWN);
        assertThat(v("code-rest-controller", "unknown-region", T)).isEqualTo(Applicability.UNKNOWN);
    }

    @Test
    void dormantExperimentIsInvalidEvenThoughMostRecent() {
        assertThat(v("pr-245-experimental", "us-prod", T)).isEqualTo(Applicability.INVALID);
    }

    @Test
    void stableArtifactsAreValidEverywhere() {
        for (String d : new String[] {"us-prod", "eu-prod", "apac-prod"}) {
            assertThat(v("code-merchant-repo", d, T)).isEqualTo(Applicability.VALID);
        }
    }

    @Test
    void judgmentsCarryProvenance() {
        Judgment j = evaluator.evaluate(catalog.byId("code-rest-controller").orElseThrow(), "us-prod", T);
        assertThat(j.evidence()).isNotEmpty().allSatisfy(e -> {
            assertThat(e.source()).isNotBlank();
            assertThat(e.scope()).isEqualTo("us-prod");
        });
    }
}
