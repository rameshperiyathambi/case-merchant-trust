package dev.caseresearch.merchant;

import java.time.Instant;

import org.springframework.stereotype.Component;

import dev.caseresearch.flags.FlagEvaluator;

/** Cohort-guarded fallback to the legacy REST implementation; only active where its flag says so. */
@Component
public class GuardedRestFallback {

    public static final String FLAG = "trust-score.rest-fallback.enabled";

    private final FlagEvaluator flags;

    public GuardedRestFallback(FlagEvaluator flags) {
        this.flags = flags;
    }

    public boolean allowed(String deployment, String cohort, Instant asOf) {
        return flags.enabledFor(deployment, FLAG, cohort, asOf);
    }
}
