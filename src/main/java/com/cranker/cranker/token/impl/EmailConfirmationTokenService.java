package com.cranker.cranker.token.impl;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.*;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.utils.AppConstants;
import com.cranker.cranker.utils.Messages;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Qualifier("emailConfirmationTokenService")
public class EmailConfirmationTokenService implements TokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenHelper helper;


    @Override
    public void confirmToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));

        helper.generalConfirm(token, TokenType.EMAIL_CONFIRMATION);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", token.getUserId()));

        if (!user.getIsVerified()) {
            userRepository.confirmEmail(user.getEmail());
        } else {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_ALREADY_CONFIRMED);
        }
    }

    @Override
    public String generateToken(User user) {
        String value = UUID.randomUUID().toString();
        Token token = new Token();
        token.setValue(value);
        token.setCreatedAt(LocalDateTime.now().toString());
        token.setExpirySeconds(AppConstants.EMAIL_TOKEN_SPAN.getValue());
        token.setUserId(user.getId());
        token.setType(TokenType.EMAIL_CONFIRMATION.getName());
        tokenRepository.save(token);
        return value;
    }
}