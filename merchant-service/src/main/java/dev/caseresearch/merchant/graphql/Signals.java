package dev.caseresearch.merchant.graphql;

import java.util.function.ToDoubleFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.caseresearch.merchant.Merchant;

/** Built-in signals for the GraphQL trust score. */
@Configuration
public class Signals {

    @Bean
    TrustSignalProvider onTimeShipping() {
        return provider("on-time-shipping", 0.5, Merchant::onTimeShippingRate);
    }

    @Bean
    TrustSignalProvider disputeRate() {
        return provider("dispute-rate", 0.3, m -> Math.max(0, 1.0 - m.disputeRate() * 10));
    }

    @Bean
    TrustSignalProvider responseTime() {
        return provider("response-time", 0.2, m -> Math.max(0, 1.0 - m.avgResponseHours() / 48.0));
    }

    private static TrustSignalProvider provider(String name, double weight, ToDoubleFunction<Merchant> value) {
        return new TrustSignalProvider() {
            public String name() { return name; }
            public double weight() { return weight; }
            public double value(Merchant m) { return value.applyAsDouble(m); }
        };
    }
}
