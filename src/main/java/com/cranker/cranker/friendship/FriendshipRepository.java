package com.cranker.cranker.friendship;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f " +
            "WHERE (f.user.id = :id OR f.friend.id = :id) " +
            "AND f.status = 'Active' ")
    Page<Friendship> findAllFriendsByUserId(Long id, Pageable pageable);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f " +
            "WHERE f.status = :status " +
            "AND ((f.user.id = :id AND f.friend.id = :friendId) OR (f.user.id = :friendId AND f.friend.id = :id)) ")
    boolean friendshipExists(Long id, Long friendId, String status);

    @Query("SELECT f FROM Friendship f WHERE  f.friend.id = :id " +
            "AND f.status = 'Pending' ")
    Page<Friendship> findAllFriendRequests(Long id, Pageable pageable);
}
