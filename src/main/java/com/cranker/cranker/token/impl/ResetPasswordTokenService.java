package com.cranker.cranker.token.impl;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.*;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Qualifier("resetPasswordTokenService")
@RequiredArgsConstructor
public class ResetPasswordTokenService implements TokenService {
    private final TokenRepository tokenRepository;
    private final TokenHelper helper;
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(this);

    @Override
    public String generateToken(User user) {
        String value = UUID.randomUUID().toString();
        Token token = new Token();
        token.setExpirySeconds(AppConstants.RESET_TOKEN_SPAN.getValue());
        token.setValue(value);
        token.setUserId(user.getId());
        token.setType(TokenType.RESET_PASSWORD.getName());
        tokenRepository.save(token);
        logger.info("Reset Password token created: {}", token.getValue());
        return value;
    }

    @Override
    public void confirmToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));
        helper.generalConfirm(token, TokenType.RESET_PASSWORD);
        logger.info("Reset Password token confirmed: {}", token.getValue());
    }

    public User getUserByToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));
       return userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", token.getUserId()));
    }
}
