package dev.caseresearch.caselayer.model;

/**
 * The four RQ2 conditions plus two RQ3 ablations of CASE; all share candidate pool and token budget B.
 */
public enum Condition {
    SEMANTIC_ONLY, RECENCY_FILTERED, SEMANTIC_PLUS_RAW_CONFIG, CASE_VALIDITY_PACKAGE,
    CASE_NO_FLAG_EVIDENCE, CASE_NO_LINEAGE_EVIDENCE
}
