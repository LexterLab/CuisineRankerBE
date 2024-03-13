package com.cranker.cranker.email;

import com.cranker.cranker.user.User;
import com.cranker.cranker.utils.Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;


@Component
@AllArgsConstructor
public class EmailSender {

    private final JavaMailSender sender;
    private final TemplateEngine engine;
    private final Properties properties;

    public void sendConfirmationEmail(User user, String confirmationLink, String code) throws MessagingException {
        Context context = new Context(Locale.ENGLISH, Map.of("firstName", user.getFirstName(),
                "link", confirmationLink, "code", code));
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(properties.getEmailSender());
        helper.setText(engine.process("email-confirmation", context), true);
        helper.setTo(user.getEmail());
        helper.setSubject("Email Confirmation");
        sender.send(message);
    }

    public void sendResetPasswordEmail(User user, String link) throws MessagingException {
        Context context = new Context(Locale.ENGLISH, Map.of("firstName", user.getFirstName(), "link", link));
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(properties.getEmailSender());
        helper.setText(engine.process("reset-password", context), true);
        helper.setTo(user.getEmail());
        helper.setSubject("Reset Your Password");
        sender.send(message);
    }

    public void sendChangedPasswordEmail(User user) throws MessagingException {
        Context context = new Context(Locale.ENGLISH, Map.of("firstName", user.getFirstName()));
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(properties.getEmailSender());
        helper.setText(engine.process("change-password", context), true);
        helper.setTo(user.getEmail());
        helper.setSubject("Your password has been changed");
        sender.send(message);
    }

    public void sendChangeEmailRequestEmail(User user, String link, String recipient) throws MessagingException {
        Context context = new Context(Locale.ENGLISH, Map.of("firstName", user.getFirstName(), "link", link));
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(properties.getEmailSender());
        helper.setText(engine.process("change-email", context), true);
        helper.setTo(recipient);
        helper.setSubject("Request to change Email");
        sender.send(message);
    }

    public void sendChangedEmailEmail(String recipient, String recipientName) throws MessagingException {
        Context context = new Context(Locale.ENGLISH, Map.of("firstName", recipientName));
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setFrom(properties.getEmailSender());
        helper.setText(engine.process("changed-email", context), true);
        helper.setTo(recipient);
        helper.setSubject("Your Email has been changed");
        sender.send(message);
    }
}