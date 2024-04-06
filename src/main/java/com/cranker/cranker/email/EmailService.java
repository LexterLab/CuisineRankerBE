package com.cranker.cranker.email;

import com.cranker.cranker.user.User;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class EmailService {

    private final EmailSender sender;


    @Async
    @Transactional
    public void sendConfirmationEmail(User user, String link, String code) throws MessagingException {
        sender.sendConfirmationEmail(user, link, code);
    }

    @Async
    @Transactional
    public void sendResetPasswordEmail(User user, String link) throws MessagingException {
        sender.sendResetPasswordEmail(user, link);
    }

    @Async
    @Transactional
    public void sendChangedPasswordEmail(User user) throws MessagingException {
        sender.sendChangedPasswordEmail(user);
    }

    @Async
    public void sendChangeEmailRequestEmail(User user, String link, String recipient) throws MessagingException {
        sender.sendChangeEmailRequestEmail(user, link, recipient);
    }

    @Async
    @Transactional
    public void sendChangedEmailEmail(String[] recipients, String recipientName) throws MessagingException {
        sender.sendChangedEmailEmail(recipients, recipientName);
    }

    @Async
    @Transactional
    public void sendTwoFactorStatusEmail(User user) throws MessagingException {
        sender.sendTwoFactorStatusEmail(user);
    }

    @Async
    @Transactional
    public void sendTwoFactorEmail(User user, String code) throws MessagingException {
        sender.sendTwoFactorEmail(user, code);
    }

    @Async
    @Transactional
    public void sendEmailConfirmationResend(User user, String confirmationLink) throws MessagingException {
        sender.sendEmailConfirmationResend(user, confirmationLink);
    }
}
