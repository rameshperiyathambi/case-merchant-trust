package dev.caseresearch.merchant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MerchantRepositoryTest {

    private final MerchantRepository repo = new MerchantRepository();

    @Test
    void findsKnownMerchant() {
        assertThat(repo.findById("m-1001")).isPresent();
    }

    @Test
    void unknownMerchantIsEmpty() {
        assertThat(repo.findById("nope")).isEmpty();
    }
}
