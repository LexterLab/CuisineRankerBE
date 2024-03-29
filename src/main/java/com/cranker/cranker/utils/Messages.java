package com.cranker.cranker.utils;

public interface Messages {
    String USER_SUCCESSFULLY_REGISTERED = "Your Account has been created successfully.";
    String PASSWORDS_DONT_MATCH = "Password don't match";
    String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";
    String EMAIL_EXISTS = "Email already exists!";
    String INVALID_JWT_TOKEN = "{\"message\": \"Invalid JWT Token\"}";
    String EXPIRED_JWT_TOKEN = "{\"message\": \"Session expired. You have been signed out\"}";
    String UNSUPPORTED_JWT_TOKEN = "{\"message\": \"Unsupported JWT Token\"}";
    String JWT_CLAIM_EMPTY = "{\"message\": \"JWT claim string is empty\"}";
    String INCORRECT_CREDENTIALS = "Incorrect email or password. Please try again.";
    String NOT_PERMITTED = "User has insufficient permissions to access";
    String TOKEN_ALREADY_CONFIRMED = "Token was already confirmed";
    String EMAIL_ALREADY_CONFIRMED = "Email was already confirmed";
    String WRONG_TOKEN_TYPE = "Provided token is not for this type of operation";
    String OLD_PASSWORD_WRONG = "Provided current password is incorrect";
    String PASSWORD_NOT_CHANGED = "Please provide a new password";
}
