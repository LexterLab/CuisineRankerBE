package com.cranker.cranker.integration.authentication;

import com.cranker.cranker.authentication.AuthenticationController;
import com.cranker.cranker.authentication.AuthenticationService;
import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.authentication.jwt.JwtRefreshRequestDTO;
import com.cranker.cranker.authentication.payload.ChangePasswordRequestDTO;
import com.cranker.cranker.authentication.payload.ForgotPasswordRequestDTO;
import com.cranker.cranker.authentication.payload.LoginRequestDTO;
import com.cranker.cranker.authentication.payload.SignUpRequestDTO;
import com.cranker.cranker.email.EmailService;
import com.cranker.cranker.user.User;
import com.cranker.cranker.utils.Messages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(jsonPath("$.refreshToken").exists());
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

//    @Test
//    void shouldRespondWithCreatedAndAccessTokensWhenProvidedValidRefreshToken() throws Exception {
//        JwtRefreshRequestDTO requestDTO = new JwtRefreshRequestDTO("refresh_token");
//
//        when(authenticationService.refreshToken(any()))
//                .thenReturn(new JWTAuthenticationResponse("access_token", "bearer",
//                        "refresh_token"));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.accessToken").exists())
//                .andExpect(jsonPath("$.tokenType").exists())
//                .andExpect(jsonPath("$.refreshToken").exists())
//                .andExpect(jsonPath("$.refreshToken").value(requestDTO.refreshToken()));
//    }

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
}
