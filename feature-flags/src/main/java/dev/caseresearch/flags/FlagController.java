package dev.caseresearch.flags;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** {@code GET /flags?deployment=us-prod&asOf=2026-06-01T00:00:00Z} returns the flag state at time t. */
@RestController
public class FlagController {

    private final FlagRepository repository;

    public FlagController(FlagRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/flags")
    public ResponseEntity<FlagSnapshot> flags(@RequestParam String deployment,
                                              @RequestParam(required = false) Instant asOf) {
        return repository.asOf(deployment, asOf == null ? Instant.now() : asOf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
