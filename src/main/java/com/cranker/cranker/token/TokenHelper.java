package com.cranker.cranker.token;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.utils.Messages;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class TokenHelper {

    private TokenRepository tokenRepository;
    private final Logger logger = LogManager.getLogger(this);
    private void validateToken(Token token, TokenType type){
        if (token.getConfirmedAt() != null) {
            logger.error(Messages.TOKEN_ALREADY_CONFIRMED + ": {}", token.getValue());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.TOKEN_ALREADY_CONFIRMED);
        }

        if(!token.getType().equalsIgnoreCase(type.getName())) {
            logger.error(Messages.WRONG_TOKEN_TYPE + ": {}", token.getType());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.WRONG_TOKEN_TYPE);
        }
    }

    public void generalConfirm(Token token, TokenType type) {
        validateToken(token, type);
        token.setConfirmedAt(LocalDateTime.now().toString());
        tokenRepository.save(token);
    }
}
