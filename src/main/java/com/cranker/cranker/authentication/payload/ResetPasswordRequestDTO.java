package com.cranker.cranker.authentication.payload;

import com.cranker.cranker.authentication.payload.validator.PasswordValueMatches;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
@PasswordValueMatches.List({
        @PasswordValueMatches(
                field = "newPassword",
                fieldMatch = "matchingPassword"
        )
})
public record ResetPasswordRequestDTO(
        @Schema(example = "!User123")
        @NotEmpty(message = "Please enter a new password.")
        @Size(min = 8, message = "Password must be at least 8 characters long.")
        @Size(max = 100, message = "Password must not exceed 100 characters.")
        String newPassword,

        @Schema(example = "!User123")
        @NotEmpty(message = "Please enter a matching password.")
        @Size(min = 8, message = "Matching Password should be at least 8 characters long.")
        @Size(max = 100, message = "Matching Password must not exceed 100 characters.")
        String matchingPassword
) {
}
