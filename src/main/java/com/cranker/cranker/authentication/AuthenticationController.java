package com.cranker.cranker.authentication;

import com.cranker.cranker.jwt.JWTAuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Login and Register REST APIs for Authentication Resource")
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
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("signout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(authenticationService.logout());
    }
}
