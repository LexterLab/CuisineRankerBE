package com.cranker.cranker.exception;

import com.cranker.cranker.utils.ErrorDetails;
import com.cranker.cranker.utils.Messages;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(APIException exception){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, exception.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleDefaultException(Exception exception){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> errorMessages = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName =  ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMessages.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(){
        ErrorDetails errorDetails = new ErrorDetails(Messages.INCORRECT_CREDENTIALS);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(){
        ErrorDetails errorDetails = new ErrorDetails(Messages.NOT_PERMITTED);
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(DataIntegrityViolationException exception){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorDetails> handleMissingServletRequestPartException(MissingServletRequestPartException exception){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDetails> handleIOException(IOException exception) {
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(GeneralSecurityException.class)
    public ResponseEntity<ErrorDetails> handleGeneralSecurityException(GeneralSecurityException exception){
        ErrorDetails errorDetails = new ErrorDetails(exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
