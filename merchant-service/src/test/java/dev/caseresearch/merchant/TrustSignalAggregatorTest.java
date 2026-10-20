package dev.caseresearch.merchant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.caseresearch.merchant.graphql.TrustSignalAggregator;
import dev.caseresearch.merchant.graphql.TrustSignalProvider;

class TrustSignalAggregatorTest {

    private static TrustSignalProvider fixed(String name, double weight, double value) {
        return new TrustSignalProvider() {
            public String name() { return name; }
            public double weight() { return weight; }
            public double value(Merchant m) { return value; }
        };
    }

    @Test
    void weightedAverageOfSignals() {
        var agg = new TrustSignalAggregator(List.of(fixed("a", 1, 1.0), fixed("b", 1, 0.0)));
        var score = agg.aggregate(new Merchant("x", "X", "US", 1, 0, 0, 0));
        assertThat(score.score()).isEqualTo(50.0);
        assertThat(score.signals()).hasSize(2);
    }

    @Test
    void newSignalsPlugInWithoutChangingAggregator() {
        var agg = new TrustSignalAggregator(List.of(fixed("a", 1, 1.0), fixed("new-signal", 1, 1.0)));
        assertThat(agg.aggregate(new Merchant("x", "X", "US", 1, 0, 0, 0)).score()).isEqualTo(100.0);
    }
}
