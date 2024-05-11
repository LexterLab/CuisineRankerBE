package com.cranker.cranker.unit.friendship;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.friendship.Friendship;
import com.cranker.cranker.friendship.FriendshipHelper;
import com.cranker.cranker.friendship.FriendshipStatus;
import com.cranker.cranker.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FriendshipHelperUnitTest {

    @InjectMocks
    private FriendshipHelper friendshipHelper;

    @Test
    void shouldPassPendingFriendshipRequestValidation() {
        User user = new User();
        user.setId(1L);

        Friendship friendship = new Friendship();
        friendship.setId(1L);
        friendship.setFriend(user);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);

       assertDoesNotThrow(() -> friendshipHelper
               .validatePendingFriendshipRequest(user, friendship, friendship.getId()));
    }

    @Test
    void shouldThrowAPIExceptionWhenPendingFriendshipRequestIsNotPending() {
        User user = new User();
        user.setId(1L);

        Friendship friendship = new Friendship();
        friendship.setId(1L);
        friendship.setFriend(user);
        friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);

        assertThrows(APIException.class, () -> friendshipHelper
                .validatePendingFriendshipRequest(user, friendship, friendship.getId()));
    }

    @Test
    void shouldThrowAPIExceptionWhenUserDoesNotBelongToPendingFriendshipRequest() {
        User actualFriend = new User();
        actualFriend.setId(1L);

        User impostor = new User();
        impostor.setId(2L);

        Friendship friendship = new Friendship();
        friendship.setId(1L);
        friendship.setFriend(actualFriend);

        assertThrows(APIException.class, () ->  friendshipHelper
                .validatePendingFriendshipRequest(impostor, friendship, friendship.getId()));
    }
}
