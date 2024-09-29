package com.cranker.cranker.notification.service.helper;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.notification.model.Notification;
import com.cranker.cranker.notification.payload.mapper.NotificationMapper;
import com.cranker.cranker.notification.repository.NotificationRepository;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationHelper {
    private final Logger logger = LogManager.getLogger(this);
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    public void checkIfNotificationBelongsToUser(Notification notification, User user) {
        if (!notification.getUser().equals(user)) {
            logger.error("Notification {} does not belong to user {}", notification, user.getEmail());
            throw new APIException(HttpStatus.CONFLICT, Messages.NOTIFICATION_USER_MISMATCH);
        }
    }

    public void sendMessage(Notification notification, User user) {
        logger.info("Sending message to {}", user.getEmail());

        messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/notifications",
                NotificationMapper.INSTANCE.entityToDto(notificationRepository.save(notification)));
        logger.info("Message sent");
    }
}
