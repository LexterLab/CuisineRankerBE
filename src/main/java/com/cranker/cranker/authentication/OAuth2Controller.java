package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.token.payload.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@Tag(name = "OAuth2 REST APIs for social authentication")
@RequestMapping("api/v1/oauth2")
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @Operation(
            summary = "Callback Google Authentication REST API",
            description = "Callback Google Authentication REST API is used to get google user's bearer token"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 200 CREATED"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST")
    })
    @PostMapping("code/google")
    public ResponseEntity<JWTAuthenticationResponse> handleGoogleLogin(@RequestBody TokenDTO tokenDTO) throws GeneralSecurityException, IOException {
        return new ResponseEntity<>(oAuth2Service.signInWithGoogle(tokenDTO), HttpStatus.CREATED);
    }
}
