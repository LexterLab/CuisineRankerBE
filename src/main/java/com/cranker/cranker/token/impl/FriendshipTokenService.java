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

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Qualifier("friendshipTokenService")
public class FriendshipTokenService implements TokenService {
    private final TokenHelper tokenHelper;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final Logger logger = LogManager.getLogger(this);

    @Override
    public String generateToken(User user) {
        Token token = new Token();
        token.setValue(UUID.randomUUID().toString());
        token.setType(TokenType.FRIENDSHIP.getName());
        token.setUserId(user.getId());
        token.setCreatedAt(LocalDateTime.now().toString());
        token.setExpirySeconds(AppConstants.FRIENDSHIP_TOKEN_SPAN.getValue());
        logger.info("Generated friendship token:{} for {}", token.getValue(), user.getEmail());
        return tokenRepository.save(token).getValue();
    }

    @Override
    public void confirmToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));

        tokenHelper.generalConfirm(token, TokenType.FRIENDSHIP);
        logger.info("Friendship Token confirmed: {}", token.getValue());
    }

    @Override
    public User getUserByToken(String value) {
        Token token = tokenRepository.findById(value)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "value", value));
        return userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", token.getUserId()));
    }
}
