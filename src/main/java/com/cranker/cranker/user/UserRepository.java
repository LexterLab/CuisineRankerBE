package com.cranker.cranker.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmailIgnoreCase(String email);

    Boolean existsByEmailIgnoreCase(String email);
    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.isVerified = TRUE WHERE u.email = ?1")
    void confirmEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.isTwoFactorEnabled = ?2 WHERE u.email = ?1")
    void switchTwoFactorAuth(String email, boolean isTwoFactorEnabled);
}
