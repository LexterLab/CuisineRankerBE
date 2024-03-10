package com.cranker.cranker.integration.recipe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RecipeControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithOKAndReturnUserPersonalRecipesWhenProvidedValidAuthenticationHeader() throws Exception {
        mockMvc.perform(get("/api/v1/recipes/personal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    @WithMockUser(username = "user2@gmail.com", roles = "USER")
    void shouldRespondWithOKAndReturnEmptyRecipeListWhenUserHasNoNE() throws Exception {
        mockMvc.perform(get("/api/v1/recipes/personal"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnForbiddenWhenNoUserProvided() throws  Exception {
        mockMvc.perform(get("/api/v1/recipes/personal"))
                .andExpect(status().isForbidden());
    }
}
