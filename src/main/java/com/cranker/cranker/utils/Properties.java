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
}
