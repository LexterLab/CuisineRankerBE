package com.cranker.cranker.integration.recipe;

import com.cranker.cranker.integration.BaseIntegrationTest;
import com.cranker.cranker.recipe.payload.RecipeRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class RecipeControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void shouldReturnUnAuthorizedWhenNoUserProvided() throws Exception {
        mockMvc.perform(delete("/api/v1/recipes/personal/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNoContentWhenProvidedValidParamsDeletingPersonalRecipe() throws Exception {
        mockMvc.perform(delete("/api/v1/recipes/personal/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenProvidedUnExistingIdDeletingPersonalRecipe() throws Exception {
        mockMvc.perform(delete("/api/v1/recipes/personal/23"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithCreatedAndCreatedRecipeData() throws Exception {
        long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken With Preparation", "Preparation", "https://www.youtube.com/",
                        1, 1, Map.of(ingredientId, amount)
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recipes/personal")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Fried Chicken With Preparation"))
                .andExpect(jsonPath("$.pictureURL").value("https://www.youtube.com/"))
                .andExpect(jsonPath("$.preparation").value("Preparation"))
                .andExpect(jsonPath("$.type").value("Personal"))
                .andExpect(jsonPath("$.prepTimeInMinutes").value("1"))
                .andExpect(jsonPath("$.cookTimeInMinutes").value("1"))
                .andExpect(jsonPath("$.totalTime").value("2"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldRespondWithUnauthorizedWhenNoUserProvidedWhenCreatingRecipe() throws Exception {
        long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken With Preparation", "Preparation", "https://www.youtube.com/",
                        1, 1, Map.of(ingredientId, amount)
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recipes/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "unexsting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnexistingUserCreatesRecipe() throws Exception {
        long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken With Preparation", "Preparation", "https://www.youtube.com/",
                        1, 1, Map.of(ingredientId, amount)
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recipes/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenUserAlreadyHasPersonalRecipeWithTheSameName() throws Exception {
        long ingredientId = 1L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Chicken Breasts", "Preparation", "https://www.youtube.com/",
                        1, 1, Map.of(ingredientId, amount)
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recipes/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestWhenProvidedInvalidIngredientAmountWhenCreatingRecipe() throws Exception {
        long ingredientId = 1L;
        double amount = -1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken Preparation", "Preparation", "https://www.youtube.com/",
                        1, 1, Map.of(ingredientId, amount)
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recipes/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenProvidedUnexistingRecipeIdentifier() throws Exception {
        long ingredientId = 1123123131L;
        double amount = 1.0;

        RecipeRequestDTO requestDTO = new RecipeRequestDTO
                (
                        "Fried Chicken Preparation", "Preparation", "https://www.youtube.com/",
                        1, 1, Map.of(ingredientId, amount)
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/recipes/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }
}
