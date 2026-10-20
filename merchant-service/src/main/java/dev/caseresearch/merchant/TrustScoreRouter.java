package dev.caseresearch.merchant;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.caseresearch.flags.FlagEvaluator;

/**
 * Chooses which trust-score implementation serves a request in this deployment, based on feature flags.
 * New trust-score work MUST go to whichever path the flags select for the target deployment.
 */
@Component
public class TrustScoreRouter {

    public static final String GRAPHQL_FLAG = "trust-score.graphql.enabled";
    public static final String REST_FLAG = "trust-score.rest.enabled";

    private final FlagEvaluator flags;
    private final GuardedRestFallback fallback;
    private final String deployment;
    private final Instant asOf;

    public TrustScoreRouter(FlagEvaluator flags, GuardedRestFallback fallback,
                            @Value("${merchant.deployment:us-prod}") String deployment,
                            @Value("${merchant.as-of:}") String asOf) {
        this.flags = flags;
        this.fallback = fallback;
        this.deployment = deployment;
        this.asOf = asOf == null || asOf.isBlank() ? null : Instant.parse(asOf);
    }

    public TrustPath route(String cohort) {
        Instant t = asOf == null ? Instant.now() : asOf;
        if (flags.enabledFor(deployment, GRAPHQL_FLAG, cohort, t)) {
            return TrustPath.GRAPHQL;
        }
        if (flags.enabledFor(deployment, REST_FLAG, cohort, t)) {
            return TrustPath.REST;
        }
        if (fallback.allowed(deployment, cohort, t)) {
            return TrustPath.REST_FALLBACK;
        }
        return TrustPath.NONE;
    }

    public String deployment() {
        return deployment;
    }
}
