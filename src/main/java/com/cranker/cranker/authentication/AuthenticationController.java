package com.cranker.cranker.authentication;

import com.cranker.cranker.jwt.JWTAuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
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
    public ResponseEntity<String> logout() {
        authenticationService.logout();
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Sign Up REST API",
            description = "Sign Up REST API  is used to create a new user"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping("signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequestDTO requestDTO) throws MessagingException {
        String response = authenticationService.signUp(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Verify Email  REST API",
            description = "Verify Email  REST API is used to verify user's email"
    )
    @ApiResponse(
            responseCode = "204",
            description = "Http Status 204 NO CONTENT"
    )
    @PatchMapping("confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam String value)  {
        authenticationService.confirmEmail(value);
        return ResponseEntity.noContent().build();
    }
}
