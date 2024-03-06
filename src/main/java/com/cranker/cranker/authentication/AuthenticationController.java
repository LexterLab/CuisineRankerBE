package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JwtRefreshRequestDTO;
import com.cranker.cranker.authentication.payload.*;
import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication REST APIs for Authentication Resource")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Login User REST API",
            description = "Login User REST API is used to get user's bearer token"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST")
})
    @PostMapping("signin")
    public ResponseEntity<JWTAuthenticationResponse> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        return ResponseEntity.ok(authenticationService.login(loginDTO));
    }

    @Operation(
            summary = "Logout User REST API",
            description = "Logout User REST API is used to clear context"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Http Status 204 NO CONTENT"
    )
    @PostMapping("signout")
    public ResponseEntity<Void> logout() {
        authenticationService.logout();
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Sign Up REST API",
            description = "Sign Up REST API  is used to create a new user"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 201 CREATED"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST")
    })
    @PostMapping("signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDTO requestDTO) throws MessagingException {
        String response = authenticationService.signUp(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Verify Email REST API",
            description = "Verify Email  REST API is used to verify user's email"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "204", description = "Http Status 204 NO CONTENT"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PatchMapping("confirm-email")
    public ResponseEntity<Void> confirmEmail(@RequestParam String value) {
        authenticationService.confirmEmail(value);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Forgot password REST API",
            description = "Forgot password REST API is used to send reset password url to user's email"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 201 CREATED"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PostMapping("forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO requestDTO) throws MessagingException {
        authenticationService.forgotPassword(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Operation(
            summary = "Reset password REST API",
            description = "Reset password REST API is used to  reset user's password with new one"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "204", description = "Http Status 204 NO CONTENT"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PatchMapping("reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String value, @Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        authenticationService.resetPassword(value, requestDTO);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Refresh token REST API",
            description = "Reset password REST API is used to refresh user's access token"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 201 CREATED"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PostMapping("refresh-token")
    public ResponseEntity<JWTAuthenticationResponse> refreshToken(@RequestBody @Valid JwtRefreshRequestDTO requestDTO) {
        JWTAuthenticationResponse response = authenticationService.refreshToken(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Change password REST API",
            description = "Change password REST API is used to change user's password"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "204", description = "Http Status 204 NO CONTENT"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO,
                                               Authentication authentication) throws MessagingException {
        authenticationService.changePassword(requestDTO, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
