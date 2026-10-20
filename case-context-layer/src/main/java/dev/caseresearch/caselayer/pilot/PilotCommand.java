package dev.caseresearch.caselayer.pilot;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** {@code --case.pilot.run=true [--case.pilot.out=pilot/out]} runs the pilot harness. */
@Component
@ConditionalOnProperty("case.pilot.run")
public class PilotCommand implements ApplicationRunner {

    private final PilotRunner runner;
    private final String out;

    public PilotCommand(PilotRunner runner, @Value("${case.pilot.out:pilot/out}") String out) {
        this.runner = runner;
        this.out = out;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        runner.run(Path.of(out));
        System.out.println("Pilot output written to " + Path.of(out).toAbsolutePath());
    }
}
