package com.cranker.cranker.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class APIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public APIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
