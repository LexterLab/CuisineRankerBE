package com.cranker.cranker.utils;

public interface Messages {
    String USER_SUCCESSFULLY_REGISTERED = "Your Account has been created successfully.";
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
    String OLD_EMAIL_WRONG = "Provided old email is incorrect";
    String EMAIL_NOT_CHANGED = "Please provide a new email";
    String ENABLED_2FA = "User has enabled 2FA, 2FA code was sent to email: ";
    String TOKEN_DONT_MATCH_USER = "Provided token doesn't match the user";
    String UNSUPPORTED_EXTENSION = "Provided extension  is unsupported";
    String NOT_VERIFIED = "You need to verify your email first!";
    String PASSWORDS_DONT_MATCH = "Passwords don't match";
    String UNSUCCESSFUL_GCS_OPERATION = "Unsuccessful GCS operation";
    String EMPTY_FILE_NAME = "Empty File Name";
    String RECIPE_EXISTS = "Recipe with this name already exists";
    String INVALID_INGREDIENT_AMOUNT = "Invalid Ingredient Amount (must be a positive number)";
    String FRIENDSHIP_ALREADY_PENDING = "Friendship request is already pending";
    String FRIENDSHIP_ALREADY_ACTIVE = "Friendship  is already active";
    String FRIENDSHIP_BLOCKED = "User has been blocked";
    String FRIENDSHIP_USER_DONT_MATCH = "User doesn't match friendship request";
    String FRIENDSHIP_REQUEST_NOT_PENDING = "Friendship request is not pending";
    String ADDING_YOURSELF_AS_FRIEND = "You cannot add yourself as a friend";
    String REMOVE_INACTIVE_FRIENDSHIP = "Cannot remove inactive friendship";
}
