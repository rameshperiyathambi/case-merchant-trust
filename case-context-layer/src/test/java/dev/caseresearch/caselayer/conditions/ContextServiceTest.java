package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.Claim;
import dev.caseresearch.caselayer.packaging.ContextPackage;
import dev.caseresearch.caselayer.packaging.Section;
import dev.caseresearch.caselayer.packaging.SectionKind;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ContextServiceTest {

    private static final String TASK = "Extend the merchant trust score with a new return rate signal";
    private static final Instant T = Instant.parse("2026-06-01T00:00:00Z");

    @Autowired
    ContextService service;

    private ContextPackage pkg(String deployment, Condition c) {
        return service.build(new TaskRequest(TASK, deployment, T, c, null, null));
    }

    @Test
    void caseActsOnGraphqlInUs() {
        assertThat(pkg("us-prod", Condition.CASE_VALIDITY_PACKAGE).actionableIds())
                .contains("code-aggregator").doesNotContain("code-rest-controller", "pr-245-experimental");
    }

    @Test
    void caseActsOnRestInEuAfterRollback() {
        assertThat(pkg("eu-prod", Condition.CASE_VALIDITY_PACKAGE).actionableIds())
                .contains("code-rest-service").doesNotContain("code-aggregator");
    }

    @Test
    void apacCohortGoesToVerify() {
        ContextPackage p = pkg("apac-prod", Condition.CASE_VALIDITY_PACKAGE);
        assertThat(p.sections()).anySatisfy(s -> {
            assertThat(s.kind()).isEqualTo(SectionKind.VERIFY);
            assertThat(s.claims()).extracting(Claim::artifactId).contains("code-aggregator");
        });
        assertThat(p.actionableIds()).doesNotContain("code-aggregator");
    }

    @Test
    void invalidArtifactsAppearOnlyAsLabeledExplanation() {
        ContextPackage p = pkg("us-prod", Condition.CASE_VALIDITY_PACKAGE);
        Section explain = p.sections().stream().filter(s -> s.kind() == SectionKind.EXPLAIN).findFirst().orElseThrow();
        assertThat(explain.claims()).extracting(Claim::artifactId).contains("pr-101-rest");
        assertThat(p.toMarkdown()).contains("NOT an implementation blueprint").contains("[INVALID]");
    }

    @Test
    void unknownDeploymentSaysNoValidPatternWhereNoneExists() {
        ContextPackage p = service.build(new TaskRequest(TASK, "unknown-region", T, null, null, null));
        assertThat(p.notes()).anySatisfy(n -> assertThat(n).contains("No valid prescriptive pattern"));
    }

    @Test
    void semanticBaselineIgnoresDeployment() {
        assertThat(pkg("us-prod", Condition.SEMANTIC_ONLY).actionableIds())
                .isEqualTo(pkg("eu-prod", Condition.SEMANTIC_ONLY).actionableIds())
                .contains("code-rest-controller");
    }

    @Test
    void rawConfigBaselineIncludesSnapshotWithoutAdjudication() {
        ContextPackage p = pkg("eu-prod", Condition.SEMANTIC_PLUS_RAW_CONFIG);
        assertThat(p.sections()).extracting(Section::kind).contains(SectionKind.CONFIG);
        assertThat(p.actionableIds()).contains("code-aggregator");
    }

    @Test
    void allConditionsRespectTheSameBudget() {
        for (Condition c : Condition.values()) {
            ContextPackage p = service.build(new TaskRequest(TASK, "us-prod", T, c, 400, null));
            assertThat(p.usedTokens()).as(c.name()).isLessThanOrEqualTo(400);
        }
    }

    @Test
    void artifactsNewerThanSnapshotTimeAreExcluded() {
        ContextPackage p = service.build(new TaskRequest(TASK, "us-prod", Instant.parse("2026-03-01T00:00:00Z"),
                Condition.SEMANTIC_ONLY, null, null));
        assertThat(p.actionableIds()).doesNotContain("pr-245-experimental", "pr-231-eu-rollback");
    }
}
