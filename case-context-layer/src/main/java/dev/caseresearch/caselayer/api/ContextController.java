package dev.caseresearch.caselayer.api;

import dev.caseresearch.caselayer.conditions.ContextService;
import dev.caseresearch.caselayer.model.TaskRequest;
import dev.caseresearch.caselayer.packaging.ContextPackage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** {@code POST /context-packages?format=json|markdown} with body {task, deployment, asOf, condition, budgetTokens}. */
@RestController
public class ContextController {

    private final ContextService service;

    public ContextController(ContextService service) {
        this.service = service;
    }

    @PostMapping("/context-packages")
    public ResponseEntity<?> build(@RequestBody TaskRequest request,
                                   @RequestParam(defaultValue = "json") String format) {
        ContextPackage pkg = service.build(request);
        if ("markdown".equalsIgnoreCase(format)) {
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/markdown")).body(pkg.toMarkdown());
        }
        return ResponseEntity.ok(pkg);
    }
}
