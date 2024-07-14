package com.cranker.cranker.notification.repository;

import com.cranker.cranker.notification.model.Notification;
import com.cranker.cranker.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Page<Notification> findAllByUser(User user, Pageable pageable);
}
