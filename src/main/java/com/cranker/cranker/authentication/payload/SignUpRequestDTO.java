package com.cranker.cranker.authentication.payload;

import com.cranker.cranker.authentication.payload.validator.PasswordValueMatches;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

@PasswordValueMatches.List({
        @PasswordValueMatches(
                field = "password",
                fieldMatch = "matchingPassword"
        )
})
public record SignUpRequestDTO(
        @Schema(example = "James")
        @NotEmpty(message = "Please enter a first name.")
        @Length(min = 1, max = 50, message = "First name must not exceed 50 characters.")
        @Pattern(regexp = "^[A-Za-z'-. ]{1,50}$", message = "Please enter a valid first name.")
        String firstName,

        @Schema(example = "Bond")
        @NotEmpty(message = "Please enter a last name.")
        @Length(min = 1, max = 50, message = "Last name must not exceed 50 characters.")
        @Pattern(regexp = "^[A-Za-z'-. ]{1,50}$", message = "Please enter a valid last name.")
        String lastName,

        @Schema(example = "!User123")
        @NotEmpty(message = "Please enter a password.")
        @Size(min = 8, message = "Password must be at least 8 characters long.")
        @Size(max = 100, message = "Password must not exceed 100 characters.")
        String password,

        @Schema(example = "!User123")
        @NotEmpty(message = "Please enter a matching password.")
        @Size(min = 8, message = "Matching Password should be at least 8 characters long.")
        @Size(max = 100, message = "Matching Password must not exceed 100 characters.")
        String matchingPassword,

        @Schema(example = "jamesbond@gmail.com")
        @NotEmpty(message = "Please enter an email address.")
        @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Please enter a valid email address.")
        String email
) {}
