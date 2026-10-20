package dev.caseresearch.merchant.graphql;

import java.util.List;

import org.springframework.stereotype.Component;

import dev.caseresearch.merchant.Merchant;

/** Aggregates every registered {@link TrustSignalProvider} into the GraphQL trust score. */
@Component
public class TrustSignalAggregator {

    private final List<TrustSignalProvider> providers;

    public TrustSignalAggregator(List<TrustSignalProvider> providers) {
        this.providers = providers;
    }

    public TrustScore aggregate(Merchant m) {
        List<TrustSignal> signals = providers.stream()
                .map(p -> new TrustSignal(p.name(), p.weight(), p.value(m))).toList();
        double totalWeight = signals.stream().mapToDouble(TrustSignal::weight).sum();
        double weighted = signals.stream().mapToDouble(s -> s.weight() * s.value()).sum();
        return new TrustScore(totalWeight == 0 ? 0 : 100 * weighted / totalWeight, signals);
    }
}
