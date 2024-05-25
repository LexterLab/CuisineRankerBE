package com.cranker.cranker.user.repository;

import com.cranker.cranker.user.model.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
}
