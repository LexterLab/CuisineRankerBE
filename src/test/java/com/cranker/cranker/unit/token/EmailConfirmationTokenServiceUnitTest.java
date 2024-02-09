package com.cranker.cranker.unit.token;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.Token;
import com.cranker.cranker.token.TokenHelper;
import com.cranker.cranker.token.TokenRepository;
import com.cranker.cranker.token.TokenType;
import com.cranker.cranker.token.impl.EmailConfirmationTokenService;
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
public class EmailConfirmationTokenServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenHelper tokenHelper;

    @InjectMocks
    private EmailConfirmationTokenService emailConfirmationTokenService;



    @Test
    public void shouldConfirmTokenWhenProvidedValidToken() {
        String tokenValue = "valid_token";
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUserId(1L);

        User user = new User();
        user.setEmail("test@example.com");
        user.setIsVerified(false);

        when(tokenRepository.findById(tokenValue)).thenReturn(Optional.of(token));
        when(userRepository.findById(token.getUserId())).thenReturn(Optional.of(user));

        emailConfirmationTokenService.confirmToken(tokenValue);

        verify(tokenHelper).generalConfirm(token, TokenType.EMAIL_CONFIRMATION);
        verify(userRepository).confirmEmail(user.getEmail());

    }

    @Test
    public void shouldThrowResourceNotFoundWhenGivenInvalidValue() {
        String invalidValue = "invalid_token";

        when(tokenRepository.findById(invalidValue)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> emailConfirmationTokenService.confirmToken(invalidValue));
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
