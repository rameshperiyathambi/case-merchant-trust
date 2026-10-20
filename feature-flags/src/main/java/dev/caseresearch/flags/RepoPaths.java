package dev.caseresearch.flags;

import java.nio.file.Files;
import java.nio.file.Path;

/** Locates the repository root: the configured path, else the nearest ancestor holding {@code flags/} and {@code context/}. */
public final class RepoPaths {

    private RepoPaths() {
    }

    public static Path repoRoot(String configured) {
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured).toAbsolutePath().normalize();
        }
        for (Path dir = Path.of(System.getProperty("user.dir")).toAbsolutePath(); dir != null; dir = dir.getParent()) {
            if (Files.isDirectory(dir.resolve("flags")) && Files.isDirectory(dir.resolve("context"))) {
                return dir;
            }
        }
        throw new IllegalStateException("Cannot locate repo root; set -Dcase.repo-root=<path>");
    }
}
