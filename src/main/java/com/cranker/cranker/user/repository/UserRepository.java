package com.cranker.cranker.user.repository;

import com.cranker.cranker.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT u FROM User u WHERE (:name IS NULL OR CONCAT(LOWER(u.firstName), ' ', LOWER(u.lastName)) LIKE %:name%) " +
            "AND NOT EXISTS (SELECT f FROM Friendship f WHERE (f.user = :currentUser AND f.friend = u) " +
            "OR (f.user = u AND f.friend = :currentUser)) " +
            "AND u != :currentUser")
    Page<User> findAllByNameAndNotFriends(String name, User currentUser, Pageable pageable);
}
