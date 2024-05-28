package com.cranker.cranker.authentication;

import com.cranker.cranker.token.payload.TokenDTO;
import com.cranker.cranker.utils.Properties;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class OAuth2Helper {
    private final Properties properties;

    public GoogleIdToken.Payload getGoogleIdTokenPayload(TokenDTO tokenDTO) throws GeneralSecurityException, IOException {
        String idTokenString = tokenDTO.value();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(properties.getGoogleClientId()))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
         return idToken.getPayload();
    }
}
