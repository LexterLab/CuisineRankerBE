package com.cranker.cranker.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class Properties {
    @Value("${email.from}")
    private String emailSender;
    @Value("${email.url}")
    private String emailURL;
    @Value("${reset.url}")
    private String resetURL;
    @Value("${change.email.url}")
    private String changeEmailURL;
    @Value("${gcp.config.file}")
    private String gcpConfigFile;
    @Value("${spring.cloud.gcp.project-id}")
    private String gcpProjectId;
    @Value("${gcp.bucket.id}")
    private String gcpBucketId;
    @Value("${recipe.default.pic}")
    private String defaultRecipePicture;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
}
