package dev.caseresearch.merchant.graphql;

import java.util.List;

public record TrustScore(double score, List<TrustSignal> signals) {
}
