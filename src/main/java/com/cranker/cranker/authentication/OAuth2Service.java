package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.authentication.jwt.JwtType;
import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.token.payload.TokenDTO;
import com.cranker.cranker.user.model.SocialUser;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.model.constant.AvailableProvider;
import com.cranker.cranker.user.repository.SocialUserRepository;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.utils.Messages;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationHelper authenticationHelper;
    private final OAuth2Helper oAuth2Helper;
    private final Logger logger = LogManager.getLogger(this);

    @Transactional
    public JWTAuthenticationResponse signInWithGoogle(TokenDTO tokenDTO) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload =  oAuth2Helper.getGoogleIdTokenPayload(tokenDTO);
        String providerId = payload.getSubject();
        String email = payload.getEmail();


        SocialUser socialUser = socialUserRepository.findByProviderId(providerId).orElseGet(() -> {
            if (userRepository.existsByEmailIgnoreCase(email)) {
                logger.error("User with email {} already exists", email);
                throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_EXISTS);
            }

            User user = new User();
            user.setEmail(payload.getEmail());
            user.setFirstName(payload.get("given_name") == null ? "Google" : (String) payload.get("given_name"));
            user.setLastName(payload.get("family_name") == null ? "Google" : (String) payload.get("family_name"));
            user = authenticationHelper.setRoles(user);
            authenticationHelper.setPictures(user);
            user = userRepository.save(user);
            userRepository.confirmEmail(user.getEmail());
            SocialUser newSocialUser = new SocialUser();
            newSocialUser.setProviderId(providerId);
            newSocialUser.setUser(user);
            newSocialUser.setProvider(AvailableProvider.GOOGLE);
            return socialUserRepository.save(newSocialUser);
        });

        logger.info("Successfully signed in with google for user {}", email);

        JWTAuthenticationResponse response = new JWTAuthenticationResponse();
        response.setAccessToken(tokenProvider.generateToken(socialUser.getUser().getEmail(), JwtType.ACCESS));
        response.setRefreshToken(tokenProvider.generateToken(socialUser.getUser().getEmail(), JwtType.REFRESH));

        return response;
    }
}
