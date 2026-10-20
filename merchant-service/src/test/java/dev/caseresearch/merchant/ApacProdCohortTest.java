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

/** apac-prod: REST for everyone; GraphQL only for the beta-merchants cohort, on both transports. */
@SpringBootTest(properties = {"merchant.deployment=apac-prod", "merchant.as-of=2026-06-01T00:00:00Z"})
@AutoConfigureMockMvc
class ApacProdCohortTest {

    private static final String QUERY = "{\"query\":\"{ merchant(id: \\\"m-1003\\\") { trustScore { score } } }\"}";

    @Autowired
    MockMvc mvc;

    @Test
    void defaultCohortGetsRestAndNoGraphql() throws Exception {
        mvc.perform(get("/api/v1/merchants/m-1003/trust-score")).andExpect(status().isOk());
        mvc.perform(post("/graphql").contentType(MediaType.APPLICATION_JSON).content(QUERY))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void betaCohortGetsGraphql() throws Exception {
        mvc.perform(post("/graphql").header("X-Cohort", "beta-merchants")
                        .contentType(MediaType.APPLICATION_JSON).content(QUERY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.merchant.trustScore.score").isNumber());
    }
}
