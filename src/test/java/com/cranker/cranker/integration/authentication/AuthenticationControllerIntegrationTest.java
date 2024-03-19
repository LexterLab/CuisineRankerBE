package com.cranker.cranker.integration.authentication;

import com.cranker.cranker.authentication.payload.*;
import com.cranker.cranker.email.EmailService;
import com.cranker.cranker.token.TokenService;
import com.cranker.cranker.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EmailService emailService;


    @BeforeEach
    void setUp() throws MessagingException {
        doNothing().when(emailService).sendChangedPasswordEmail(any(User.class));
        doNothing().when(emailService).sendChangeEmailRequestEmail(any(User.class), any(String.class), any(String.class));
    }
    @Test
    void shouldRespondWithNoContentWhenLoggedOut() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRespondWithOkAndAccessTokensWhenProvidedValidCredentialsOnSignIn() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO("user@gmail.com", "!user123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.isTwoFactor").exists());
    }

    @Test
    void shouldRespondWithBadRequestWhenProvidedInvalidCredentialsOnSignIn() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO("invalid@gmail.com", "!user123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithBadRequestWhenProvidedExistingEmailOnSignUp() throws  Exception {
        SignUpRequestDTO requestDTO = new SignUpRequestDTO("James",
                "Bond", "!User123", "!User123", "user@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRespondWithNotFoundWhenProvidedUnExistingEmailWhenRequestingForgotPassword() throws Exception {
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO("fake@gmail.com");


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNoContentWhenProvidedValidAuthenticationAndOldPassword() throws Exception {
        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO("!user123",
                "newPassword", "newPassword");



        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestWhenProvidedWrongOldPassword() throws Exception {
        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO("wrong",
                "newPassword", "newPassword");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestWhenProvidedSamePasswordAsOldOne() throws Exception  {
        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO("!user123",
                "!user123", "!user123");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@gmail2.com", roles = "USER")
    void shouldRespondWithNotFoundWhenProvidedInvalidAuthenticationUser() throws Exception {
        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO("!user123",
                "newPassword", "newPassword");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRespondWithForbiddenWhenUserNotSignedIn() throws Exception {
        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO("!user123",
                "newPassword", "newPassword");

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestWhenRequestingChangeEmailWithWrongOldEmail() throws Exception {
        ChangeEmailRequestDTO requestDTO = new ChangeEmailRequestDTO("user2@gmail.com", "new@gmail.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithBadRequestWhenRequestingChangeEmailWithIdenticalEmails() throws Exception {
        ChangeEmailRequestDTO requestDTO = new ChangeEmailRequestDTO("user@gmail.com", "user@gmail.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithForbiddenWhenUserNotProvidedWhenRequestingChangeEmail() throws Exception {
        ChangeEmailRequestDTO requestDTO = new ChangeEmailRequestDTO("user@gmail.com", "user@gmail.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = "USER")
    void shouldRespondWithNoContentWhenChanging2FAMode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/two-factor"))
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(username = "admin@gmail.com", roles = "USER")
    void shouldRespondWithBadWhenChanging2FAModeWithUnverifiedUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/two-factor"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondWithForbiddenWhenUserNotProvidedWhenChanging2FAMode() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/auth/two-factor"))
                .andExpect(status().isForbidden());
    }
}
