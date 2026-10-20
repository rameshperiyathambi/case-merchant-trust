package dev.caseresearch.flags;

import java.util.List;

/**
 * State of one flag.
 *
 * @param scope              ALL (whole deployment, the default) or COHORT (only listed cohorts)
 * @param assignmentVerified whether cohort membership has been verified against the assignment service
 */
public record FlagEntry(boolean enabled, Scope scope, List<String> cohorts, boolean assignmentVerified) {

    public enum Scope { ALL, COHORT }

    public FlagEntry {
        scope = scope == null ? Scope.ALL : scope;
        cohorts = cohorts == null ? List.of() : cohorts;
    }
}
