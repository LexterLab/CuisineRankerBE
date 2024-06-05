package com.cranker.cranker.integration.ingredient;

import com.cranker.cranker.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IngredientControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithPagedIngredientsAndOKStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients?name=Oiled"))
                .andExpect(jsonPath("$.ingredients").exists())
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithEmptyPagedIngredientsAndOKStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ingredients?name=banana"))
                .andExpect(jsonPath("$.ingredients").exists())
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients").isEmpty())
                .andExpect(status().isOk());
    }

}
