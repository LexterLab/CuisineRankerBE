package com.cranker.cranker.token;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.utils.Messages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class TokenHelper {

    private TokenRepository tokenRepository;

    private void validateToken(Token token, TokenType type){
        if (token.getConfirmedAt() != null) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.TOKEN_ALREADY_CONFIRMED);
        }

        if(!token.getType().equalsIgnoreCase(type.getName())) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.WRONG_TOKEN_TYPE);
        }
    }

    public void generalConfirm(Token token, TokenType type) {
        validateToken(token, type);
        token.setConfirmedAt(LocalDateTime.now().toString());
        tokenRepository.save(token);
    }
}
