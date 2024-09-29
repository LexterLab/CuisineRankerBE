package com.cranker.cranker.unit.notification;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.notification.model.Notification;
import com.cranker.cranker.notification.payload.NotificationRequestDTO;
import com.cranker.cranker.notification.repository.NotificationRepository;
import com.cranker.cranker.notification.service.NotificationService;
import com.cranker.cranker.notification.service.helper.NotificationHelper;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationHelper notificationHelper;



    @Test
    void shouldSendNotification() {

        User user = new User();
        user.setEmail("user@gmail.com");

        NotificationRequestDTO request = NotificationRequestDTO.builder()
                .title("Friend request")
                .message("New friendship request")
                .build();

        doNothing().when(notificationHelper)
                .sendMessage(any(Notification.class), any(User.class));

        notificationService.sendNotification(request, user);

        verify(notificationHelper).sendMessage(any(Notification.class), any(User.class));
    }

    @Test
    void shouldDismissNotification() {
        String email = "user@gmail.com";
        Long userId = 1L;
        Long notificationId = 2L;

        User user = new User();
        user.setId(userId);
        user.setEmail(email);

        Notification notification = new Notification();
        notification.setTitle("Friend request");
        notification.setMessage("New friendship request");
        notification.setUser(user);
        notification.setId(notificationId);

        when(notificationRepository.findById(any())).thenReturn(Optional.of(notification));
        when(userRepository.findUserByEmailIgnoreCase(any())).thenReturn(Optional.of(user));
        doNothing().when(notificationHelper).sendMessage(any(Notification.class), any(User.class));

        notificationService.dismissNotification(user.getEmail(), notification.getId());

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnknownUserDismissesNotification() {
        String email = "unknown@gmail.com";
        Long notificationId = 2L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.dismissNotification(email, notificationId));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDismissingUnknownNotification() {
        String email = "user@gmail.com";
        User user = new User();
        user.setEmail(email);

        Long notificationId = 2L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(notificationRepository.findById(notificationId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.dismissNotification(email, notificationId));
    }

    @Test
    void shouldThrowAPIExceptionWhenDismissingForeignNotification() {
        String email = "user@gmail.com";
        User user = new User();
        user.setEmail(email);

        User realuser = new User();
        realuser.setEmail("realuser@gmail.com");

        Long notificationId = 2L;
        Notification notification = Notification.builder()
                .message("Hey there")
                .title("Greetings!")
                .id(notificationId)
                .user(realuser)
                .build();

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        doThrow(APIException.class).when(notificationHelper).checkIfNotificationBelongsToUser(notification, user);

        assertThrows(APIException.class, () -> notificationService.dismissNotification(email, notificationId));
    }
}