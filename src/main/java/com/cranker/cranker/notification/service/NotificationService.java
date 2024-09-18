package com.cranker.cranker.notification.service;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.notification.model.Notification;
import com.cranker.cranker.notification.payload.NotificationRequestDTO;
import com.cranker.cranker.notification.payload.NotificationResponseDTO;
import com.cranker.cranker.notification.payload.mapper.NotificationMapper;
import com.cranker.cranker.notification.repository.NotificationRepository;
import com.cranker.cranker.notification.service.helper.NotificationHelper;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.utils.PageableUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PageableUtil pageableUtil;
    private final NotificationHelper notificationHelper;
    private final SimpMessagingTemplate messagingTemplate;
    private final Logger logger = LogManager.getLogger(this);

    @Transactional
    public void sendNotification(NotificationRequestDTO requestDTO, User user) {
        logger.info("Sending notification: {}", requestDTO );
        Notification notification = NotificationMapper.INSTANCE.notificationRequestToEntity(requestDTO);
        notification.setUser(user);

        logger.info("Notification sent: {}", notification );

        sendMessage(notification, user);
    }

    private void sendMessage(Notification notification, User user) {
        logger.info("Sending message to {}", user.getEmail());

        messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/notifications",
                NotificationMapper.INSTANCE.entityToDto(notificationRepository.save(notification)));
        logger.info("Message sent");
    }

    @Transactional
    public NotificationResponseDTO retrieveNotifications(String email, int pageNo, int pageSize, String sortBy,
                                                         String sortDir) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, sortDir);

        Page<Notification> notifications = notificationRepository.findAllByUser(user, pageable);

        logger.info("Retrieving notifications from {}", user.getEmail());

        return NotificationMapper.INSTANCE.pageToNotificationResponse(notifications,
                NotificationMapper.INSTANCE.entityToDto(notifications.getContent()));
    }

    @Transactional
    public void dismissNotification(String email, Long notificationId) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("notification", "id", notificationId));

        notificationHelper.checkIfNotificationBelongsToUser(notification, user);

        notificationRepository.delete(notification);

        sendMessage(notification, user);

        logger.info("Dismissing notification from {}", user.getEmail());
    }

}
