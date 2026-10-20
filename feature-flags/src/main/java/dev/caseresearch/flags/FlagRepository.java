package dev.caseresearch.flags;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Loads versioned flag snapshots from {@code flags/<deployment>/<date>.yaml}. */
@Component
public class FlagRepository {

    private record RawSnapshot(String deployment, Instant effectiveAt, String authority, Map<String, FlagEntry> flags) {
    }

    private final List<FlagSnapshot> snapshots = new ArrayList<>();

    @Autowired
    public FlagRepository(@Value("${case.repo-root:}") String configuredRoot) {
        Path root = RepoPaths.repoRoot(configuredRoot);
        try (Stream<Path> files = Files.walk(root.resolve("flags"))) {
            for (Path file : files.filter(p -> p.toString().endsWith(".yaml")).toList()) {
                RawSnapshot raw = YamlFiles.read(file, RawSnapshot.class);
                snapshots.add(new FlagSnapshot(raw.deployment(), raw.effectiveAt(),
                        raw.authority() == null ? "UNKNOWN" : raw.authority(),
                        root.relativize(file).toString().replace('\\', '/'), raw.flags(), Files.readString(file)));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read flag snapshots", e);
        }
        snapshots.sort(Comparator.comparing(FlagSnapshot::effectiveAt));
    }

    /** Latest snapshot for the deployment with effectiveAt &lt;= asOf. */
    public Optional<FlagSnapshot> asOf(String deployment, Instant asOf) {
        FlagSnapshot best = null;
        for (FlagSnapshot s : snapshots) {
            if (s.deployment().equals(deployment) && !s.effectiveAt().isAfter(asOf)) {
                best = s;
            }
        }
        return Optional.ofNullable(best);
    }

    public List<String> deployments() {
        return snapshots.stream().map(FlagSnapshot::deployment).distinct().sorted().toList();
    }
}
