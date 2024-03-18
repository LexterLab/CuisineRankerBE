package com.cranker.cranker.token.impl;

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

import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Qualifier("twoFactorTokenService")
public class TwoFactorTokenService implements TokenService {
    private final TokenHelper helper;
    private final TokenRepository tokenRepository;
    private final Logger logger = LogManager.getLogger(this);
    @Override
    public String generateToken(User user) {
        Random random = new Random();
        int value = random.nextInt(9000) + 1000;
        Token token = new Token();
        token.setValue(value + "");
        token.setType(TokenType.TWO_FACTOR.getName());
        token.setUserId(user.getId());
        token.setCreatedAt(LocalDateTime.now().toString());
        token.setExpirySeconds(AppConstants.TWO_FACTOR_TOKEN_SPAN.getValue());
        tokenRepository.save(token);
        logger.info("Email change token created: {}", value);
        return value + "";
    }

    @Override
    public void confirmToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));

        helper.generalConfirm(token, TokenType.TWO_FACTOR);
        logger.info("Two Factor Authentication Token confirmed: {}", token.getValue());
    }
}
