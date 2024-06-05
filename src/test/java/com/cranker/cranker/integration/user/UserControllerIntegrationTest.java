package com.cranker.cranker.integration.user;

import com.cranker.cranker.friendship.Friendship;
import com.cranker.cranker.friendship.FriendshipDTO;
import com.cranker.cranker.integration.BaseIntegrationTest;
import com.cranker.cranker.token.payload.TokenDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest extends BaseIntegrationTest {
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

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithCreatedAndPendingFriendshipWhenRequestingFriendship() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/4")
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.friendName").value("friendly user"))
                .andExpect(jsonPath("$.status").value("Pending"))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenUnauthenticatedUserRequestsFriendship() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenNonExistingUserRequestsFriendship() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenFriendshipRequestAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenPendingRequestAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenRequestingBlockedFriendship() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithOKAndUserPendingFriendshipRequests() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends/requests")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendships").isArray());
    }

    @Test
    @WithMockUser(username = "unexsting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenNonExistingUserRetrievesPendingFriendshipRequests() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends/requests"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithForbiddenWhenUnauthenticatedUserRetrievesPendingFriendships() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithOKAndUserSentPendingFriendshipRequests() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends/requests/sent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendships").isArray());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenNonExistingUserRetrievesSentPendingFriendshipRequests() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends/requests/sent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithForbiddenWhenUnAuthenticatedUserRetrievesSentPendingFriendshipRequests() throws Exception {
        mockMvc.perform(get("/api/v1/users/friends/requests/sent"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "smilingperson@gmail.com", roles = "USER")
    void shouldRespondWithAcceptedFriendshipRequestAndOKStatus() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendName").value("user user"))
                .andExpect(jsonPath("$.status").value("Active"));
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnExistingUserAcceptedFriendshipRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenUnauthenticatedUserAcceptedFriendshipRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "smilingperson@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenAcceptingUnExistingFriendshipRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenUserDoesNotMatchFriendshipRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/4").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "usertwo@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenAcceptingNonPendingFriendshipRequest() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/2").contentType(MediaType.APPLICATION_JSON))
         .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "smilingperson@gmail.com", roles = "USER")
    void shouldRespondWithNoContentWhenRejectingFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/4/reject").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenNonExistingUserDoesNotAcceptFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/4/reject"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenUnAuthenticatedUserDoesNotAcceptFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/4/reject"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenNotMatchingUserRejectsFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/4/reject"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "usertwo@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenRejectingNotPendingFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/2/reject"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithOKAndAvailableUsersToAdd() throws Exception {
        mockMvc.perform(get("/api/v1/users/search?name=user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").exists());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnExistingUserSearchesUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users/search?name=user"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithForbiddenWhenUnAuthenticatedUserSearchesUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users/search?name=user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnExistingUserGeneratesFriendshipToken() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenUnAuthenticatedUserGeneratesFriendshipToken() throws Exception {
        mockMvc.perform(post("/api/v1/users/friends/token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenUnAuthenticatedUserActivatesFriendshipToken() throws Exception {
        mockMvc.perform(patch("/api/v1/users/friends/token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnExistingUserActivatesFriendshipToken() throws Exception {
        TokenDTO requestDTO = new TokenDTO("token");
        mockMvc.perform(patch("/api/v1/users/friends/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNoContentWhenCancellingSentFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/requests/" + 4 +"/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "unexisting@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenUnExistingUserCancellingSentFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/requests/4/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithUnAuthorizedWhenUnAuthenticatedUserCancellingSentFriendshipRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/requests/4/cancel"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithConflictWhenCancellingSomeoneElseFriendshipSentRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/requests/5/cancel"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNotFoundWhenCancellingUnexistingFriendshipSentRequest() throws Exception {
        mockMvc.perform(delete("/api/v1/users/friends/requests/0/cancel"))
                .andExpect(status().isNotFound());
    }
}
