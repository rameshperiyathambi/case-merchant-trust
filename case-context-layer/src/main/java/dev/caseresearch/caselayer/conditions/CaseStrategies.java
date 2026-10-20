package dev.caseresearch.caselayer.conditions;

import dev.caseresearch.caselayer.applicability.ApplicabilityEvaluator;
import dev.caseresearch.caselayer.graph.Ablation;
import dev.caseresearch.caselayer.model.Condition;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registers CASE and its two RQ3 ablations as strategies. */
@Configuration
public class CaseStrategies {

    @Bean
    ContextStrategy caseValidity(ApplicabilityEvaluator e) {
        return new CaseValidityStrategy(e, Condition.CASE_VALIDITY_PACKAGE, Ablation.NONE);
    }

    @Bean
    ContextStrategy caseNoFlagEvidence(ApplicabilityEvaluator e) {
        return new CaseValidityStrategy(e, Condition.CASE_NO_FLAG_EVIDENCE, Ablation.NO_FLAG_EVIDENCE);
    }

    @Bean
    ContextStrategy caseNoLineageEvidence(ApplicabilityEvaluator e) {
        return new CaseValidityStrategy(e, Condition.CASE_NO_LINEAGE_EVIDENCE, Ablation.NO_LINEAGE_EVIDENCE);
    }
}
