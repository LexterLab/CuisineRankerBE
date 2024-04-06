package com.cranker.cranker.unit.email;

import com.cranker.cranker.email.EmailSender;
import com.cranker.cranker.user.User;
import com.cranker.cranker.utils.Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailSenderUnitTest {

    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private TemplateEngine engine;

    @Mock
    private Properties properties;

    @InjectMocks
    private EmailSender emailSender;

    @AfterEach
    void tearDown() {
        reset(engine, properties);
    }

    @Test
    void shouldSendConfirmationEmail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");


        String confirmationLink = "https://example.com/confirm";
        String code = "123456";


        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendConfirmationEmail(user, confirmationLink, code);


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendResetPasswordEmail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");


        String resetLink = "https://example.com/confirm";


        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendResetPasswordEmail(user, resetLink);


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendChangedPasswordEmail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");




        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendChangedPasswordEmail(user);


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendChangeEmailRequestMail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");




        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendChangeEmailRequestEmail(user, "link", "recipient@gmail.com");


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendChangedEmailMail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");




        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        String[] recipients =  {"recipient@gmail.com", "recipient2@gmail.com"};
        emailSender.sendChangedEmailEmail(recipients, "recipientName");


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendChanged2FAModeMail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");
        user.setIsTwoFactorEnabled(false);



        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendTwoFactorStatusEmail(user);


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSend2FAMail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");
        user.setIsTwoFactorEnabled(false);
        String code = "5631";


        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendTwoFactorEmail(user, code);


        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendResendConfirmationEmail() throws MessagingException {
        User user = new User();
        user.setFirstName("John");
        user.setEmail("test@example.com");


        String confirmationLink = "https://example.com/confirm";


        when(properties.getEmailSender()).thenReturn("sender@example.com");


        String emailContent = "Mock email content";
        when(engine.process(anyString(), any(Context.class))).thenReturn(emailContent);


        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);


        emailSender.sendEmailConfirmationResend(user, confirmationLink);


        verify(javaMailSender).send(any(MimeMessage.class));
    }
}
