package dev.caseresearch.merchant.rest;

public record LegacyTrustScore(String merchantId, int score, double onTimeRate, double disputeRate) {
}
