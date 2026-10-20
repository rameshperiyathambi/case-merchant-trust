package dev.caseresearch.flags;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class FlagRepositoryTest {

    private final FlagRepository repo = new FlagRepository("");

    @Test
    void picksLatestSnapshotAtOrBeforeT() {
        assertThat(repo.asOf("eu-prod", Instant.parse("2025-10-01T00:00:00Z")).orElseThrow().source())
                .isEqualTo("flags/eu-prod/2025-09-01.yaml");
        assertThat(repo.asOf("eu-prod", Instant.parse("2026-03-01T00:00:00Z")).orElseThrow().source())
                .isEqualTo("flags/eu-prod/2026-01-20.yaml");
        assertThat(repo.asOf("eu-prod", Instant.parse("2026-06-01T00:00:00Z")).orElseThrow().source())
                .isEqualTo("flags/eu-prod/2026-04-02.yaml");
    }

    @Test
    void noSnapshotBeforeFirstOne() {
        assertThat(repo.asOf("apac-prod", Instant.parse("2025-01-01T00:00:00Z"))).isEmpty();
    }

    @Test
    void listsDeployments() {
        assertThat(repo.deployments()).containsExactly("apac-prod", "eu-prod", "us-prod");
    }
}
