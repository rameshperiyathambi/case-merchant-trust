package dev.caseresearch.flags;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class FlagEvaluator {

    private final FlagRepository repository;

    public FlagEvaluator(FlagRepository repository) {
        this.repository = repository;
    }

    /**
     * Deployment-level status of a flag, for validity reasoning. Only authoritative, deployment-wide
     * (scope ALL) evidence yields TRUE/FALSE; cohort-scoped, non-authoritative or missing evidence is UNKNOWN.
     */
    public FlagReading reading(String deployment, String flag, Instant asOf) {
        Optional<FlagSnapshot> snap = repository.asOf(deployment, asOf);
        if (snap.isEmpty()) {
            return new FlagReading(flag, deployment, Tri.UNKNOWN, null, null, null,
                    "no flag snapshot for " + deployment + " at or before " + asOf);
        }
        FlagSnapshot s = snap.get();
        FlagEntry e = s.flags().get(flag);
        if (e == null) {
            return new FlagReading(flag, deployment, Tri.UNKNOWN, s.source(), s.effectiveAt(), s.authority(),
                    "flag not present in snapshot");
        }
        if (!s.authoritative()) {
            return new FlagReading(flag, deployment, Tri.UNKNOWN, s.source(), s.effectiveAt(), s.authority(),
                    "snapshot is not authoritative");
        }
        if (e.scope() == FlagEntry.Scope.COHORT) {
            return new FlagReading(flag, deployment, Tri.UNKNOWN, s.source(), s.effectiveAt(), s.authority(),
                    "cohort-scoped flag (" + e.cohorts() + (e.assignmentVerified() ? "" : ", assignment unverified")
                            + "); deployment-wide status undefined");
        }
        return new FlagReading(flag, deployment, e.enabled() ? Tri.TRUE : Tri.FALSE, s.source(), s.effectiveAt(),
                s.authority(), "flag " + (e.enabled() ? "enabled" : "disabled") + " for all traffic");
    }

    /** Runtime decision for one request: is the flag on for this cohort? */
    public boolean enabledFor(String deployment, String flag, String cohort, Instant asOf) {
        return repository.asOf(deployment, asOf)
                .map(s -> s.flags().get(flag))
                .map(e -> e.enabled() && (e.scope() == FlagEntry.Scope.ALL || e.cohorts().contains(cohort)))
                .orElse(false);
    }
}
