package dev.caseresearch.merchant.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import dev.caseresearch.merchant.Merchant;
import dev.caseresearch.merchant.MerchantRepository;
import dev.caseresearch.merchant.TrustPath;
import dev.caseresearch.merchant.TrustScoreRouter;

/** GraphQL resolvers for merchants and their trust score (schema: graphql/merchant.graphqls). */
@Controller
public class MerchantTrustResolver {

    private final MerchantRepository merchants;
    private final TrustSignalAggregator aggregator;
    private final TrustScoreRouter router;

    public MerchantTrustResolver(MerchantRepository merchants, TrustSignalAggregator aggregator,
                                 TrustScoreRouter router) {
        this.merchants = merchants;
        this.aggregator = aggregator;
        this.router = router;
    }

    @QueryMapping
    public Merchant merchant(@Argument String id) {
        return merchants.findById(id).orElse(null);
    }

    @SchemaMapping(typeName = "Merchant", field = "trustScore")
    public TrustScore trustScore(Merchant merchant,
                                 @ContextValue(name = CohortInterceptor.COHORT_KEY, required = false) String cohort) {
        if (router.route(cohort == null ? "default" : cohort) != TrustPath.GRAPHQL) {
            throw new IllegalStateException(
                    "GraphQL trust score is not enabled in " + router.deployment());
        }
        return aggregator.aggregate(merchant);
    }
}
