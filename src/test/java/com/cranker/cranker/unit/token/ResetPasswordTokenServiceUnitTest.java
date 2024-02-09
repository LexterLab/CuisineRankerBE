package com.cranker.cranker.unit.token;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.Token;
import com.cranker.cranker.token.TokenHelper;
import com.cranker.cranker.token.TokenRepository;
import com.cranker.cranker.token.TokenType;
import com.cranker.cranker.token.impl.ResetPasswordTokenService;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordTokenServiceUnitTest {

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenHelper tokenHelper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResetPasswordTokenService resetPasswordTokenService;


    @Test
    void shouldConfirmTokenWhenGivenValidToken() {
        String tokenValue = "valid_token";
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUserId(1L);

        when(tokenRepository.findById(tokenValue)).thenReturn(Optional.of(token));

        resetPasswordTokenService.confirmToken(tokenValue);

        verify(tokenHelper).generalConfirm(token, TokenType.RESET_PASSWORD);
    }

    @Test
    void shouldThrowResourceNotFoundWhenGivenInvalidToken() {
        String tokenValue = "invalid_token";

        when(tokenRepository.findById(tokenValue)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> resetPasswordTokenService.confirmToken(tokenValue));
        assertThrows(ResourceNotFoundException.class, () -> resetPasswordTokenService.getUserByToken(tokenValue));
    }

    @Test
    void shouldReturnUserWhenProvidedValidToken() {
        String tokenValue = "valid_token";
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUserId(1L);

        User user = new User();
        user.setEmail("test@example.com");
        user.setIsVerified(false);

        when(tokenRepository.findById(tokenValue)).thenReturn(Optional.of(token));
        when(userRepository.findById(token.getUserId())).thenReturn(Optional.of(user));

        User returnedUser = resetPasswordTokenService.getUserByToken(tokenValue);

        assertEquals(user.getId(), returnedUser.getId());
    }
    @Test
    public void shouldMatchTokenToProvidedUser() {
        User validUser = new User();
        Token token = new Token();
        token.setUserId(validUser.getId());

        when(tokenRepository.save(token)).thenReturn(token);

        Token newToken = tokenRepository.save(token);

        assertEquals(token.getUserId(), newToken.getUserId());
    }
}
