package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.authentication.jwt.JwtType;
import com.cranker.cranker.token.payload.TokenDTO;
import com.cranker.cranker.user.model.SocialUser;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.model.constant.AvailableProvider;
import com.cranker.cranker.user.repository.SocialUserRepository;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.utils.Properties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final Properties properties;
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationHelper authenticationHelper;

    @Transactional
    public JWTAuthenticationResponse signInWithGoogle(TokenDTO tokenDTO, HttpServletRequest request) throws GeneralSecurityException, IOException {
        String idTokenString = tokenDTO.value();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(properties.getGoogleClientId()))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        GoogleIdToken.Payload payload = idToken.getPayload();
        String providerId = payload.getSubject();

        SocialUser socialUser = socialUserRepository.findByProviderId(providerId).orElseGet(() -> {
            User user = new User();
            user.setEmail(payload.getEmail());
            user.setFirstName(payload.get("given_name") == null ? "Google" : (String) payload.get("given_name"));
            user.setLastName(payload.get("family_name") == null ? "Google" : (String) payload.get("family_name"));
            user = authenticationHelper.setRoles(user);
            authenticationHelper.setPictures(user);
            user = userRepository.save(user);
            SocialUser newSocialUser = new SocialUser();
            newSocialUser.setProviderId(providerId);
            newSocialUser.setUser(user);
            newSocialUser.setProvider(AvailableProvider.GOOGLE);
            return socialUserRepository.save(newSocialUser);
        });

        JWTAuthenticationResponse response = new JWTAuthenticationResponse();
        response.setAccessToken(tokenProvider.generateToken(socialUser.getUser().getEmail(), JwtType.ACCESS));
        response.setRefreshToken(tokenProvider.generateToken(socialUser.getUser().getEmail(), JwtType.REFRESH));

        return response;
    }
}
