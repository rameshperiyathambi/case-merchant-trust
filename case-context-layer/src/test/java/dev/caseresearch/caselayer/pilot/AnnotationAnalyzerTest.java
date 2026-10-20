package dev.caseresearch.caselayer.pilot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AnnotationAnalyzerTest {

    @Test
    void noAnnotationsMeansNoReport(@TempDir Path dir) throws Exception {
        assertThat(AnnotationAnalyzer.analyze(dir)).isNull();
    }

    @Test
    void computesPrecisionAndKappa(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("t1.csv"), String.join("\n",
                "artifact_id,annotatorA,annotatorB",
                "a,APPLICABLE,APPLICABLE",
                "b,INCOMPATIBLE,INCOMPATIBLE",
                "c,UNKNOWN,APPLICABLE",
                "d,APPLICABLE,APPLICABLE"));
        AnnotationAnalyzer.Report r = AnnotationAnalyzer.analyze(dir);
        assertThat(r.items()).isEqualTo(4);
        assertThat(r.agreement()).isEqualTo(0.75);
        var t = r.tasks().get(0);
        assertThat(t.validPrecisionAtK()).isEqualTo(0.5);
        assertThat(t.incompatibleAtK()).isEqualTo(0.25);
        // po=0.75; pa: A=.5,I=.25,U=.25; pb: A=.75,I=.25,U=0 -> pe=.375+.0625=.4375; kappa=(.75-.4375)/.5625
        assertThat(r.kappa()).isCloseTo(0.5556, within(0.001));
    }

    @Test
    void perfectAgreementOnOneLabelIsKappaOne(@TempDir Path dir) throws Exception {
        Files.writeString(dir.resolve("t.csv"), "artifact_id,annotatorA,annotatorB\na,APPLICABLE,APPLICABLE\n");
        assertThat(AnnotationAnalyzer.analyze(dir).kappa()).isEqualTo(1.0);
    }
}
