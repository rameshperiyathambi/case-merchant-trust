package dev.caseresearch.merchant.graphql;

import java.util.Map;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/** Copies the {@code X-Cohort} header into the GraphQL context so GraphQL routes like the REST path does. */
@Component
public class CohortInterceptor implements WebGraphQlInterceptor {

    public static final String COHORT_KEY = "cohort";

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        String cohort = request.getHeaders().getFirst("X-Cohort");
        request.configureExecutionInput((input, builder) -> builder
                .graphQLContext(Map.of(COHORT_KEY, cohort == null ? "default" : cohort)).build());
        return chain.next(request);
    }
}
