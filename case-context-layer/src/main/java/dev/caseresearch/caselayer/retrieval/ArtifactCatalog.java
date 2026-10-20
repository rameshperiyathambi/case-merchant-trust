package dev.caseresearch.caselayer.retrieval;

import dev.caseresearch.caselayer.model.Artifact;
import dev.caseresearch.caselayer.model.ArtifactRole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.caseresearch.flags.RepoPaths;
import dev.caseresearch.flags.YamlFiles;

/** Loads {@code context/artifacts.yaml} and the content of every referenced file. */
@Component
public class ArtifactCatalog {

    private record Spec(String id, String type, ArtifactRole role, String implementation, Instant updatedAt,
                        String path, String title, String summary) {
    }

    private record CatalogFile(List<Spec> artifacts) {
    }

    private final Path root;
    private final List<Artifact> artifacts;

    public ArtifactCatalog(@Value("${case.repo-root:}") String configuredRoot) throws IOException {
        this.root = RepoPaths.repoRoot(configuredRoot);
        CatalogFile file = YamlFiles.read(root.resolve("context/artifacts.yaml"), CatalogFile.class);
        this.artifacts = file.artifacts().stream().map(s -> new Artifact(s.id(), s.type(), s.role(),
                s.implementation(), s.updatedAt(), s.path(), s.title(), s.summary(), read(s.path()))).toList();
    }

    private String read(String relativePath) {
        try {
            return Files.readString(root.resolve(relativePath));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read artifact " + relativePath, e);
        }
    }

    public List<Artifact> all() {
        return artifacts;
    }

    public Optional<Artifact> byId(String id) {
        return artifacts.stream().filter(a -> a.id().equals(id)).findFirst();
    }
}
