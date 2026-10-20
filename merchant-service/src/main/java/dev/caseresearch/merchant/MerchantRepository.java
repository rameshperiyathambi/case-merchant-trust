package dev.caseresearch.merchant;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

/** Stable, deployment-independent in-memory merchant store (fake data). */
@Repository
public class MerchantRepository {

    private final Map<String, Merchant> merchants = List.of(
            new Merchant("m-1001", "Alder Goods", "US", 0.97, 0.01, 4.0, 0.02),
            new Merchant("m-1002", "Birch Wares", "EU", 0.88, 0.04, 12.0, 0.09),
            new Merchant("m-1003", "Cedar Supply", "APAC", 0.93, 0.02, 8.0, 0.05))
            .stream().collect(Collectors.toMap(Merchant::id, m -> m));

    public Optional<Merchant> findById(String id) {
        return Optional.ofNullable(merchants.get(id));
    }

    public List<Merchant> findAll() {
        return List.copyOf(merchants.values());
    }
}
