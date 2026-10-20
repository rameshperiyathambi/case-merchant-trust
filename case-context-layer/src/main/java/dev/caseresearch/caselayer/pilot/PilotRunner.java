package dev.caseresearch.caselayer.pilot;

import dev.caseresearch.caselayer.conditions.ContextService;
import dev.caseresearch.caselayer.model.Condition;
import dev.caseresearch.caselayer.model.Scored;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.ContextPackage;
import dev.caseresearch.caselayer.packaging.Section;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.caseresearch.flags.RepoPaths;
import dev.caseresearch.flags.YamlFiles;

/**
 * Read-only pilot harness (paper Section IV). For each task-deployment pair it builds the package under every
 * condition with the same pool and budget, scores it against a withheld oracle (RQ2/RQ3), exports the pool for
 * human labelling (RQ1), and runs the two-deployment replay probe. Oracles are never passed to any strategy.
 */
@Component
public class PilotRunner {

    record TaskSpec(String id, String stratum, String task, String deployment, Instant asOf, String oracle,
                    String replayDeployment) {
    }

    record Oracle(List<String> applicable, List<String> incompatible, List<String> unknown) {
        Set<String> applicableSet() {
            return set(applicable);
        }

        Set<String> incompatibleSet() {
            return set(incompatible);
        }

        Set<String> unknownSet() {
            return set(unknown);
        }

        private static Set<String> set(List<String> l) {
            return l == null ? Set.of() : new TreeSet<>(l);
        }
    }

    public record Row(String task, String stratum, String deployment, Condition condition, int actionable,
                      double validPrecision, double validRecall, int incompatible, int unknownActionable, int tokens) {
    }

    public record Probe(String task, String deploymentA, String deploymentB, Set<String> actOnA, Set<String> actOnB) {
        public boolean changed() {
            return !actOnA.equals(actOnB);
        }
    }

    public record Result(List<Row> rows, List<Probe> probes, AnnotationAnalyzer.Report annotations) {
    }

    private final ContextService service;
    private final Path root;

    public PilotRunner(ContextService service, @Value("${case.repo-root:}") String configuredRoot) {
        this.service = service;
        this.root = RepoPaths.repoRoot(configuredRoot);
    }

    public Result run(Path outDir) throws IOException {
        Path pilot = root.resolve("pilot");
        List<Row> rows = new ArrayList<>();
        List<Probe> probes = new ArrayList<>();
        List<Path> taskFiles;
        try (Stream<Path> s = Files.list(pilot.resolve("tasks"))) {
            taskFiles = s.filter(p -> p.toString().endsWith(".yaml")).sorted().toList();
        }
        for (Path tf : taskFiles) {
            TaskSpec t = YamlFiles.read(tf, TaskSpec.class);
            Oracle oracle = YamlFiles.read(pilot.resolve("oracles/" + t.oracle() + ".yaml"), Oracle.class);
            TaskRequest base = new TaskRequest(t.task(), t.deployment(), t.asOf(), null, null, null);
            List<Scored> pool = service.pool(base);
            Path dir = outDir.resolve(t.id());
            Files.createDirectories(dir);
            writePool(dir, pool);
            Set<String> applicableInPool = pool.stream().map(s -> s.artifact().id())
                    .filter(oracle.applicableSet()::contains).collect(Collectors.toCollection(TreeSet::new));
            for (Condition c : Condition.values()) {
                ContextPackage pkg = service.build(base.withCondition(c), pool);
                Set<String> act = pkg.actionableIds();
                long ok = act.stream().filter(oracle.applicableSet()::contains).count();
                long found = applicableInPool.stream().filter(act::contains).count();
                rows.add(new Row(t.id(), t.stratum(), t.deployment(), c, act.size(),
                        act.isEmpty() ? 0 : (double) ok / act.size(),
                        applicableInPool.isEmpty() ? 0 : (double) found / applicableInPool.size(),
                        (int) act.stream().filter(oracle.incompatibleSet()::contains).count(),
                        (int) act.stream().filter(oracle.unknownSet()::contains).count(), pkg.usedTokens()));
                Files.writeString(dir.resolve(c.name().toLowerCase(Locale.ROOT) + ".md"), pkg.toMarkdown());
            }
            if (t.replayDeployment() != null) {
                TaskRequest a = base.withCondition(Condition.CASE_VALIDITY_PACKAGE);
                TaskRequest b = a.withDeployment(t.replayDeployment());
                probes.add(new Probe(t.id(), a.deployment(), b.deployment(), service.build(a).actionableIds(),
                        service.build(b).actionableIds()));
            }
        }
        AnnotationAnalyzer.Report annotations = AnnotationAnalyzer.analyze(pilot.resolve("annotations"));
        writeSummary(outDir, rows, probes, annotations);
        return new Result(rows, probes, annotations);
    }

    /** RQ1 export: the top-k pool with blank columns for two independent annotators. */
    private static void writePool(Path dir, List<Scored> pool) throws IOException {
        StringBuilder sb = new StringBuilder("rank,artifact_id,relevance,annotatorA,annotatorB\n");
        for (int i = 0; i < pool.size(); i++) {
            sb.append(String.format(Locale.ROOT, "%d,%s,%.3f,,%n", i + 1, pool.get(i).artifact().id(),
                    pool.get(i).score()));
        }
        Files.writeString(dir.resolve("pool.csv"), sb.toString());
    }

    private static void writeSummary(Path outDir, List<Row> rows, List<Probe> probes,
                                     AnnotationAnalyzer.Report annotations) throws IOException {
        StringBuilder csv = new StringBuilder("task,stratum,deployment,condition,actionable,valid_precision,"
                + "valid_recall,incompatible,unknown_actionable,tokens\n");
        rows.forEach(r -> csv.append(String.format(Locale.ROOT, "%s,%s,%s,%s,%d,%.2f,%.2f,%d,%d,%d%n", r.task(),
                r.stratum(), r.deployment(), r.condition(), r.actionable(), r.validPrecision(), r.validRecall(),
                r.incompatible(), r.unknownActionable(), r.tokens())));
        Files.writeString(outDir.resolve("summary.csv"), csv.toString());

        StringBuilder md = new StringBuilder("# Pilot summary (synthetic data; illustrative, not a result)\n\n");
        md.append("Oracles are hand-written by the authors; CASE's precision is high by construction. See README.\n\n");
        md.append("| condition | mean valid precision | mean valid recall | incompatible actionable (total) "
                + "| unknown actionable (total) |\n|---|---|---|---|---|\n");
        for (Condition c : Condition.values()) {
            List<Row> cr = rows.stream().filter(r -> r.condition() == c).toList();
            md.append(String.format(Locale.ROOT, "| %s | %.2f | %.2f | %d | %d |%n", c,
                    cr.stream().mapToDouble(Row::validPrecision).average().orElse(0),
                    cr.stream().mapToDouble(Row::validRecall).average().orElse(0),
                    cr.stream().mapToInt(Row::incompatible).sum(),
                    cr.stream().mapToInt(Row::unknownActionable).sum()));
        }
        md.append("\n## Replay probe (same task and repository state, two deployments)\n\n");
        for (Probe p : probes) {
            md.append(String.format("- %s: ACT_ON under %s = %s; under %s = %s; prescriptive evidence changed: **%s**%n",
                    p.task(), p.deploymentA(), p.actOnA(), p.deploymentB(), p.actOnB(), p.changed()));
        }
        if (annotations != null) {
            md.append(String.format(Locale.ROOT, "%n## RQ1 annotations%n%n%d items, agreement %.2f, Cohen's kappa %.2f%n%n",
                    annotations.items(), annotations.agreement(), annotations.kappa()));
            annotations.tasks().forEach(t -> md.append(String.format(Locale.ROOT,
                    "- %s: k=%d, agreed=%d, valid precision@k=%.2f, incompatible@k=%.2f%n", t.task(), t.k(),
                    t.agreed(), t.validPrecisionAtK(), t.incompatibleAtK())));
        }
        Files.writeString(outDir.resolve("summary.md"), md.toString());
    }
}
