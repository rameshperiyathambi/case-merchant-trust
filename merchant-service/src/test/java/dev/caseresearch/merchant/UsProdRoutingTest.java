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

/** us-prod after 2026-01-15: GraphQL is the live path, legacy REST is retired. */
@SpringBootTest(properties = {"merchant.deployment=us-prod", "merchant.as-of=2026-06-01T00:00:00Z"})
@AutoConfigureMockMvc
class UsProdRoutingTest {

    @Autowired
    MockMvc mvc;

    @Test
    void restIsGone() throws Exception {
        mvc.perform(get("/api/v1/merchants/m-1001/trust-score")).andExpect(status().isGone());
    }

    @Test
    void graphqlServesTrustScore() throws Exception {
        mvc.perform(post("/graphql").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"{ merchant(id: \\\"m-1001\\\") { trustScore { score signals { name } } } }\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.merchant.trustScore.signals.length()").value(3));
    }
}
