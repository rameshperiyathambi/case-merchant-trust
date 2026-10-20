package dev.caseresearch.caselayer.pilot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * RQ1: reads two annotators' labels of the top-k pool ({@code pilot/annotations/<task>.csv} with columns
 * {@code artifact_id,annotatorA,annotatorB}; labels APPLICABLE | INCOMPATIBLE | UNKNOWN) and reports
 * valid precision@k, incompatible@k (on agreed labels) and Cohen's kappa.
 */
public final class AnnotationAnalyzer {

    public record TaskStats(String task, int k, int agreed, double validPrecisionAtK, double incompatibleAtK) {
    }

    public record Report(List<TaskStats> tasks, int items, double agreement, double kappa) {
    }

    private AnnotationAnalyzer() {
    }

    /** Returns null when the directory holds no annotation files. */
    public static Report analyze(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            return null;
        }
        List<Path> files;
        try (Stream<Path> s = Files.list(dir)) {
            files = s.filter(p -> p.toString().endsWith(".csv")).sorted().toList();
        }
        if (files.isEmpty()) {
            return null;
        }
        List<TaskStats> tasks = new ArrayList<>();
        List<String[]> pairs = new ArrayList<>();
        for (Path f : files) {
            List<String[]> rows = Files.readAllLines(f).stream().skip(1).filter(l -> !l.isBlank())
                    .map(l -> l.split(",", -1)).toList();
            int agreed = 0;
            int applicable = 0;
            int incompatible = 0;
            for (String[] r : rows) {
                String a = r[1].trim().toUpperCase();
                String b = r[2].trim().toUpperCase();
                pairs.add(new String[] {a, b});
                if (a.equals(b)) {
                    agreed++;
                    applicable += a.equals("APPLICABLE") ? 1 : 0;
                    incompatible += a.equals("INCOMPATIBLE") ? 1 : 0;
                }
            }
            String task = f.getFileName().toString().replace(".csv", "");
            tasks.add(new TaskStats(task, rows.size(), agreed, ratio(applicable, rows.size()),
                    ratio(incompatible, rows.size())));
        }
        long agree = pairs.stream().filter(p -> p[0].equals(p[1])).count();
        return new Report(tasks, pairs.size(), ratio((int) agree, pairs.size()), kappa(pairs));
    }

    static double kappa(List<String[]> pairs) {
        int n = pairs.size();
        if (n == 0) {
            return 0;
        }
        Map<String, Integer> a = new HashMap<>();
        Map<String, Integer> b = new HashMap<>();
        int agree = 0;
        for (String[] p : pairs) {
            a.merge(p[0], 1, Integer::sum);
            b.merge(p[1], 1, Integer::sum);
            agree += p[0].equals(p[1]) ? 1 : 0;
        }
        double po = (double) agree / n;
        double pe = 0;
        for (String label : a.keySet()) {
            pe += ((double) a.get(label) / n) * ((double) b.getOrDefault(label, 0) / n);
        }
        return pe >= 1.0 ? 1.0 : (po - pe) / (1 - pe);
    }

    private static double ratio(int num, int den) {
        return den == 0 ? 0 : (double) num / den;
    }
}
