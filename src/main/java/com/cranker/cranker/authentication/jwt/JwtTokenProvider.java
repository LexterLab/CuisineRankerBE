package com.cranker.cranker.authentication.jwt;


import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.utils.Messages;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    @Value("${app.jwt-refresh-expiration-miliseconds}")
    private long jwtRefreshExpiry;


    public String generateToken(String username, JwtType jwtType) {
        Date currentDate = new Date();
        Date expireDate;

        if (jwtType.equals(JwtType.ACCESS)) {
            expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
        } else {
            expireDate = new Date(currentDate.getTime() + jwtRefreshExpiry);
        }
        Claims claims = Jwts.claims().setSubject(username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.JWT_CLAIM_EMPTY);
        }
    }
}
