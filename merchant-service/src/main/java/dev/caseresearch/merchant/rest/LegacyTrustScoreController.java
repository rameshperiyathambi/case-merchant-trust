package dev.caseresearch.merchant.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import dev.caseresearch.merchant.MerchantRepository;
import dev.caseresearch.merchant.TrustPath;
import dev.caseresearch.merchant.TrustScoreRouter;

/**
 * Legacy REST endpoint: {@code GET /api/v1/merchants/{id}/trust-score}.
 * Well documented and tested, but only live where the flags route to REST.
 */
@RestController
public class LegacyTrustScoreController {

    private final MerchantRepository merchants;
    private final LegacyTrustScoreService service;
    private final TrustScoreRouter router;

    public LegacyTrustScoreController(MerchantRepository merchants, LegacyTrustScoreService service,
                                      TrustScoreRouter router) {
        this.merchants = merchants;
        this.service = service;
        this.router = router;
    }

    @GetMapping("/api/v1/merchants/{id}/trust-score")
    public ResponseEntity<?> trustScore(@PathVariable String id,
                                        @RequestHeader(value = "X-Cohort", defaultValue = "default") String cohort) {
        TrustPath path = router.route(cohort);
        if (path != TrustPath.REST && path != TrustPath.REST_FALLBACK) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body("REST trust-score is not active in " + router.deployment() + "; use GraphQL");
        }
        return merchants.findById(id)
                .<ResponseEntity<?>>map(m -> ResponseEntity.ok(service.compute(m)))
                .orElse(ResponseEntity.notFound().build());
    }
}
