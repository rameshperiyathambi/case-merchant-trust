package dev.caseresearch.merchant.graphql;

import dev.caseresearch.merchant.Merchant;

/** Extension point: implement this and register as a bean to add a trust signal on the GraphQL path. */
public interface TrustSignalProvider {
    String name();

    double weight();

    /** Normalised value in [0, 1]. */
    double value(Merchant merchant);
}
