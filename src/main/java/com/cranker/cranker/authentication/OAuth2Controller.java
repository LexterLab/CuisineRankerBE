package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.token.payload.TokenDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/oauth2")
public class OAuth2Controller {
    private final OAuth2Service oAuth2Service;

    @PostMapping("code/google")
    public ResponseEntity<JWTAuthenticationResponse> handleGoogleLogin(@RequestBody TokenDTO tokenDTO,
                                                                       HttpServletRequest request) throws GeneralSecurityException, IOException {
        return new ResponseEntity<>(oAuth2Service.signInWithGoogle(tokenDTO, request), HttpStatus.CREATED);
    }
}
