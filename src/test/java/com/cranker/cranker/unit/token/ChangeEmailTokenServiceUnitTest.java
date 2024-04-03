package com.cranker.cranker.unit.token;

import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.Token;
import com.cranker.cranker.token.TokenHelper;
import com.cranker.cranker.token.TokenRepository;
import com.cranker.cranker.token.TokenType;
import com.cranker.cranker.token.impl.ChangeEmailTokenService;
import com.cranker.cranker.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChangeEmailTokenServiceUnitTest {

    @Mock
    private TokenHelper tokenHelper;
    @Mock
    private TokenRepository tokenRepository;
    @InjectMocks
    private ChangeEmailTokenService changeEmailTokenService;

    @Test
    void shouldConfirmTokenWhenGivenValidToken() {
        String tokenValue = "valid_token";
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUserId(1L);

        when(tokenRepository.findById(tokenValue)).thenReturn(Optional.of(token));

        changeEmailTokenService.confirmToken(tokenValue);

        verify(tokenHelper).generalConfirm(token, TokenType.CHANGE_EMAIL);
    }

    @Test
    void shouldThrowResourceNotFoundWhenGivenInvalidToken() {
        String tokenValue = "invalid_token";

        when(tokenRepository.findById(tokenValue)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> changeEmailTokenService.confirmToken(tokenValue));

    }

}
