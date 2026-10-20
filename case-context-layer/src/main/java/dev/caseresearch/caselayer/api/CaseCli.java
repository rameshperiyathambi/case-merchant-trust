package dev.caseresearch.caselayer.api;

import dev.caseresearch.caselayer.conditions.ContextService;
import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.TaskRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * CLI mode: {@code --case.cli.task="..." --case.cli.deployment=eu-prod --case.cli.as-of=2026-06-01T00:00:00Z
 * [--case.cli.condition=CASE_VALIDITY_PACKAGE] [--case.cli.out=AGENT_CONTEXT.md]}.
 * Writes the package the agent should read before editing.
 */
@Component
@ConditionalOnProperty("case.cli.task")
public class CaseCli implements ApplicationRunner {

    private final ContextService service;
    private final String task;
    private final String deployment;
    private final String asOf;
    private final String condition;
    private final String out;

    public CaseCli(ContextService service, @Value("${case.cli.task}") String task,
                   @Value("${case.cli.deployment}") String deployment,
                   @Value("${case.cli.as-of:}") String asOf,
                   @Value("${case.cli.condition:CASE_VALIDITY_PACKAGE}") String condition,
                   @Value("${case.cli.out:AGENT_CONTEXT.md}") String out) {
        this.service = service;
        this.task = task;
        this.deployment = deployment;
        this.asOf = asOf;
        this.condition = condition;
        this.out = out;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TaskRequest req = new TaskRequest(task, deployment, asOf.isBlank() ? null : Instant.parse(asOf),
                Condition.valueOf(condition), null, null);
        Files.writeString(Path.of(out), service.build(req).toMarkdown());
        System.out.println("Wrote " + Path.of(out).toAbsolutePath());
    }
}
