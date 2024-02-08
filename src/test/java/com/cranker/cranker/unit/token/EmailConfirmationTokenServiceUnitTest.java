package com.cranker.cranker.unit.token;

import com.cranker.cranker.token.Token;
import com.cranker.cranker.token.TokenHelper;
import com.cranker.cranker.token.TokenRepository;
import com.cranker.cranker.token.TokenType;
import com.cranker.cranker.token.impl.EmailConfirmationTokenService;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.utils.AppConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationTokenServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private TokenHelper helper;

    @InjectMocks
    private EmailConfirmationTokenService service;


    @Test
    public void shouldConfirmTokenWhenProvidedValidToken() {
        String tokenValue = UUID.randomUUID().toString();
        Token token = new Token();
        token.setValue(tokenValue);
        token.setUserId(1L);
        when(tokenRepository.findById(tokenValue)).thenReturn(Optional.of(token));

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setIsVerified(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //prints false
        System.out.println(user.getIsVerified());
        // Act
        service.confirmToken(tokenValue);
        //prints false
        System.out.println(user.getIsVerified());
        // Assert
        assertTrue(user.getIsVerified());
        verify(userRepository, times(1)).confirmEmail("test@example.com");
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
