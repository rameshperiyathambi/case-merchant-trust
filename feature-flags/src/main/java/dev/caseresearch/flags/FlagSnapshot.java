package dev.caseresearch.flags;

import java.time.Instant;
import java.util.Map;

/**
 * A versioned export of flag configuration for one deployment.
 *
 * @param source   repo-relative path of the snapshot file (provenance)
 * @param authority "AUTHORITATIVE" for exports from the flag system of record; anything else is a hint only
 */
public record FlagSnapshot(String deployment, Instant effectiveAt, String authority, String source,
                           Map<String, FlagEntry> flags, String rawText) {

    public boolean authoritative() {
        return "AUTHORITATIVE".equalsIgnoreCase(authority);
    }
}
