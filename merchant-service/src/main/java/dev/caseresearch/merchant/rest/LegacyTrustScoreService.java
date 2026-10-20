package dev.caseresearch.merchant.rest;

import org.springframework.stereotype.Service;

import dev.caseresearch.merchant.Merchant;

/**
 * Legacy (v1) trust-score computation behind the REST endpoint. Fixed formula, no pluggable signals.
 * Superseded by the GraphQL path in deployments where trust-score.graphql.enabled is on (see ADR-007).
 */
@Service
public class LegacyTrustScoreService {

    public LegacyTrustScore compute(Merchant m) {
        double raw = 100 * (0.7 * m.onTimeShippingRate() + 0.3 * (1.0 - m.disputeRate() * 10));
        int score = (int) Math.max(0, Math.min(100, Math.round(raw)));
        return new LegacyTrustScore(m.id(), score, m.onTimeShippingRate(), m.disputeRate());
    }
}
