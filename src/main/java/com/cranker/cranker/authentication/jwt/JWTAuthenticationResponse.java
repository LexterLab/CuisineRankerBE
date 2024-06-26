package com.cranker.cranker.authentication.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JWTAuthenticationResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String refreshToken;
    private Boolean isTwoFactor = false;
}
