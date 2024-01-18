package com.cranker.cranker.utils;

public class Messages {
    public static final String USER_SUCCESSFULLY_REGISTERED = "Your Account has been created successfully.";
    public static final String USER_NOT_FOUND_WITH_EMAIL = "User not found with email: ";
    public static final String USERNAME_EXISTS = "Username already exists!";
    public static final String EMAIL_EXISTS = "Email already exists!";
    public static final String INVALID_JWT_TOKEN = "{\"message\": \"Invalid JWT Token\"}";
    public static final String EXPIRED_JWT_TOKEN = "{\"message\": \"Session expired. You have been signed out\"}";
    public static final String UNSUPPORTED_JWT_TOKEN = "{\"message\": \"Unsupported JWT Token\"}";
    public static final String JWT_CLAIM_EMPTY = "{\"message\": \"JWT claim string is empty\"}";
    public static final String INCORRECT_CREDENTIALS = "Incorrect email or password. Please try again.";
    public static final String NOT_PERMITTED = "User has insufficient permissions to access";
}
