package dev.caseresearch.merchant;

/** Synthetic merchant profile. */
public record Merchant(String id, String name, String region, double onTimeShippingRate, double disputeRate,
                       double avgResponseHours,
                       double returnRate) {
}
