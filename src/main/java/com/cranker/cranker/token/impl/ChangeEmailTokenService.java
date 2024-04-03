package com.cranker.cranker.token.impl;

import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.authentication.jwt.JwtType;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.*;
import com.cranker.cranker.user.User;
import com.cranker.cranker.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;



@RequiredArgsConstructor
@Service
@Qualifier("changeEmailTokenService")
public class ChangeEmailTokenService implements TokenService {
    private final TokenHelper helper;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final Logger logger = LogManager.getLogger(this);
    @Override
    public String generateToken(User user) {
        String value  = tokenProvider.generateToken(user.getEmail(), JwtType.ACCESS);
        Token token = new Token();
        token.setValue(value);
        token.setType(TokenType.CHANGE_EMAIL.getName());
        token.setUserId(user.getId());
        token.setCreatedAt(LocalDateTime.now().toString());
        token.setExpirySeconds(AppConstants.CHANGE_EMAIL_TOKEN_SPAN.getValue());
        tokenRepository.save(token);
        logger.info("Email change token created: {}", value);
        return value;
    }

    @Override
    @Transactional
    public void confirmToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));

        helper.generalConfirm(token, TokenType.CHANGE_EMAIL);
        logger.info("Change Email token confirmed: {}", token.getValue());
    }
}
