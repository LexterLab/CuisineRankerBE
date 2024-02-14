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
}
