package dev.caseresearch.merchant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** eu-prod after the 2026-04-02 regional rollback: legacy REST is live again, GraphQL is off. */
@SpringBootTest(properties = {"merchant.deployment=eu-prod", "merchant.as-of=2026-06-01T00:00:00Z"})
@AutoConfigureMockMvc
class EuProdRoutingTest {

    @Autowired
    MockMvc mvc;

    @Test
    void restServesTrustScore() throws Exception {
        mvc.perform(get("/api/v1/merchants/m-1002/trust-score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantId").value("m-1002"));
    }

    @Test
    void graphqlTrustScoreIsDisabled() throws Exception {
        mvc.perform(post("/graphql").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"{ merchant(id: \\\"m-1002\\\") { trustScore { score } } }\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
