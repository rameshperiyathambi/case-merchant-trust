package dev.caseresearch.caselayer.conditions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.ContextPackage;

@SpringBootTest
class AblationTest {

    private static final String TASK = "Extend the merchant trust score with a new return rate signal";
    private static final Instant T = Instant.parse("2026-06-01T00:00:00Z");

    @Autowired
    ContextService service;

    private ContextPackage pkg(String deployment, Condition c) {
        return service.build(new TaskRequest(TASK, deployment, T, c, null, null));
    }

    @Test
    void withoutFlagEvidence_gqlCannotBeConfirmedInUs() {
        // Supersession still proves REST is retired, but nothing confirms GraphQL is on: it becomes a VERIFY item.
        ContextPackage p = pkg("us-prod", Condition.CASE_NO_FLAG_EVIDENCE);
        assertThat(p.actionableIds()).doesNotContain("code-rest-controller", "code-aggregator");
    }

    @Test
    void withoutLineageEvidence_flagsStillCarryTheMigrationCase() {
        ContextPackage p = pkg("us-prod", Condition.CASE_NO_LINEAGE_EVIDENCE);
        assertThat(p.actionableIds()).contains("code-aggregator").doesNotContain("code-rest-controller");
    }

    @Test
    void ablationsNeverPresentIncompatibleEvidenceAsActionable() {
        for (Condition c : new Condition[] {Condition.CASE_NO_FLAG_EVIDENCE, Condition.CASE_NO_LINEAGE_EVIDENCE}) {
            assertThat(pkg("eu-prod", c).actionableIds()).as(c.name()).doesNotContain("code-aggregator");
        }
    }
}
