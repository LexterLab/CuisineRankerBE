package com.cranker.cranker.user.repository;

import com.cranker.cranker.user.model.SocialUser;
import com.cranker.cranker.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
    Optional<SocialUser> findByProviderId(String providerId);
    boolean existsByUser(User user);
}
