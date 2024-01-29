package com.cranker.cranker.token;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.utils.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenHelper {

    public void validateToken(Token token){
        if (token.getConfirmedAt() != null) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.TOKEN_ALREADY_CONFIRMED);
        }

    }
}
