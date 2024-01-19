package com.cranker.cranker.authentication;

import com.cranker.cranker.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.jwt.JwtTokenProvider;
import com.cranker.cranker.jwt.TokenType;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public JWTAuthenticationResponse login(LoginRequestDTO loginRequestDTO){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JWTAuthenticationResponse authenticationResponse = new JWTAuthenticationResponse();
        authenticationResponse.setAccessToken(tokenProvider.generateToken(authentication.getName(), TokenType.ACCESS));
        authenticationResponse.setRefreshToken(tokenProvider.generateToken(authentication.getName(), TokenType.REFRESH));
        return authenticationResponse;
    }

}
