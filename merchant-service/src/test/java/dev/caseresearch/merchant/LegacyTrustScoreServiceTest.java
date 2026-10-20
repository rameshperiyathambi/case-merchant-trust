package dev.caseresearch.merchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import dev.caseresearch.merchant.rest.LegacyTrustScoreService;

class LegacyTrustScoreServiceTest {

    private final LegacyTrustScoreService service = new LegacyTrustScoreService();

    @Test
    void perfectMerchantScoresHigh() {
        var score = service.compute(new Merchant("x", "X", "US", 1.0, 0.0, 1.0, 0.0));
        assertThat(score.score()).isEqualTo(100);
    }

    @Test
    void highDisputeRateLowersScore() {
        var good = service.compute(new Merchant("x", "X", "US", 0.9, 0.0, 1.0, 0.0));
        var bad = service.compute(new Merchant("x", "X", "US", 0.9, 0.08, 1.0, 0.0));
        assertThat(bad.score()).isLessThan(good.score());
    }
}
