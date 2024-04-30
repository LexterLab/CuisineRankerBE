package com.cranker.cranker.integration.user;

import com.cranker.cranker.user.payload.UserRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldReturnUserInfo() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("user user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.isVerified").value(true))
                .andExpect(jsonPath("$.isTwoFactorEnabled").value(false));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithOkAndUpdatedPersonalInfoWhenGivenValidAuthentication() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO("NewName", "SecondName");
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(requestDTO.firstName()))
                .andExpect(jsonPath("$.lastName").value(requestDTO.lastName()));
    }

    @Test
    @WithMockUser(username = "user2@gmail.com", roles = "USER")
    void shouldReturnNotFoundWhenProvidedInvalidUser() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO("NewName", "SecondName");
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());


    }

    @Test
    void shouldReturnForbiddenWhenNoUserProvided() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoUserProvidedWhenChangingPersonalInfo() throws Exception {
        UserRequestDTO requestDTO = new UserRequestDTO("NewName", "SecondName");
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithUserProfilePicturesAndOKStatus() throws Exception {
        mockMvc.perform(get("/api/v1/users/pictures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }
    @Test
    void shouldRespondWithForbiddenWhenRequestingUserPicturesWhileNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/pictures"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user3232@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenRequestingPicturesForNotExistingUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/pictures"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldChangeUserProfilePicture() throws Exception {
        long pictureId = 1L;
        mockMvc.perform(patch("/api/v1/users/pictures/" + pictureId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("user user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.isVerified").value(true))
                .andExpect(jsonPath("$.isTwoFactorEnabled").value(false))
                .andExpect(jsonPath("$.profilePicURL").value("https://storage.googleapis.com/cuisine-media/profile-icons/rat1.jpg"));

    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenProvidedUnExistingPictureIdentifier() throws Exception {
        long pictureId = 0L;
        mockMvc.perform(patch("/api/v1/users/pictures/" + pictureId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithOKAndUserFriendList() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.friendships").isArray())
                .andExpect(jsonPath("$.friendships").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user2@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenRetrievingUserFriendListWithUnexistingUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithForbiddenWhenRetrievingUserFriendListWithoutBeingSignedIn() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends"))
                .andExpect(status().isForbidden());
    }
}
