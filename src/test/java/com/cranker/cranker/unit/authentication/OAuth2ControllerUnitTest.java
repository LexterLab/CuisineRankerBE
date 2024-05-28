package com.cranker.cranker.unit.authentication;

import com.cranker.cranker.authentication.OAuth2Controller;
import com.cranker.cranker.authentication.OAuth2Service;
import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.token.payload.TokenDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OAuth2ControllerUnitTest {

    @Mock
    private OAuth2Service oAuth2Service;

    @InjectMocks
    private OAuth2Controller controller;

    @Test
    void shouldRespondWithCreatedAndJWTsWhenAuthenticatingWithGoogleSocialLogin() throws GeneralSecurityException, IOException {
        TokenDTO tokenDTO = new TokenDTO("token");

        String refreshToken = "refreshToken";
        String accessToken = "accessToken";

        JWTAuthenticationResponse jwtAuthenticationResponse = new JWTAuthenticationResponse();
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        jwtAuthenticationResponse.setAccessToken(accessToken);

        when(oAuth2Service.signInWithGoogle(tokenDTO)).thenReturn(jwtAuthenticationResponse);

        ResponseEntity<JWTAuthenticationResponse> response = controller.handleGoogleLogin(tokenDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(jwtAuthenticationResponse, response.getBody());
    }
}
