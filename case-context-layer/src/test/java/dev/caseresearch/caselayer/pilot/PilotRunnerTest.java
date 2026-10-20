package dev.caseresearch.caselayer.pilot;

import dev.caseresearch.caselayer.model.Condition;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PilotRunnerTest {

    @Autowired
    PilotRunner runner;

    @Test
    void caseNeverPresentsIncompatibleEvidenceAsActionable(@TempDir Path out) throws Exception {
        PilotRunner.Result r = runner.run(out);
        assertThat(r.rows()).filteredOn(x -> x.condition() == Condition.CASE_VALIDITY_PACKAGE)
                .hasSize(6).allSatisfy(x -> {
                    assertThat(x.incompatible()).as(x.task()).isZero();
                    assertThat(x.unknownActionable()).as(x.task()).isZero();
                });
    }

    @Test
    void similarityBaselineSuffersOnMigratedAndRegionalTasks(@TempDir Path out) throws Exception {
        PilotRunner.Result r = runner.run(out);
        assertThat(r.rows()).filteredOn(x -> x.condition() == Condition.SEMANTIC_ONLY
                        && !x.stratum().equals("stable"))
                .anySatisfy(x -> assertThat(x.incompatible()).isPositive());
    }

    @Test
    void replayProbeChangesPrescriptiveEvidenceAcrossDeployments(@TempDir Path out) throws Exception {
        PilotRunner.Result r = runner.run(out);
        assertThat(r.probes()).isNotEmpty().allSatisfy(p -> assertThat(p.changed()).as(p.task()).isTrue());
        assertThat(out.resolve("summary.csv")).exists();
        assertThat(out.resolve("summary.md")).exists();
    }
}
