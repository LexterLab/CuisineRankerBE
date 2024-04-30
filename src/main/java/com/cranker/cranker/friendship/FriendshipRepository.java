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
}
