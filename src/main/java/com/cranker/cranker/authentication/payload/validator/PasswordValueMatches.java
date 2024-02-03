package com.cranker.cranker.authentication.payload.validator;

import com.cranker.cranker.utils.Messages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class )
public @interface PasswordValueMatches {
    String message() default Messages.PASSWORDS_DONT_MATCH;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String field();

    String fieldMatch();
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        PasswordValueMatches[] value();
    }
};
