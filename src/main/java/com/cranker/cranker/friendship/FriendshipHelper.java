package com.cranker.cranker.friendship;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.user.User;
import com.cranker.cranker.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FriendshipHelper {
    private final Logger logger = LogManager.getLogger(this);
    private final FriendshipRepository friendshipRepository;

    public void validatePendingFriendshipRequest(User user, Friendship friendship, Long friendshipId) {
        if (!friendship.getFriend().getId().equals(user.getId())) {
            logger.error("User: {} doesn't match the friend in friendship: {}", user.getId(), friendshipId);
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_USER_DONT_MATCH);
        } else if (!friendship.getStatus().equals(FriendshipStatus.PENDING.getName())) {
            logger.error(Messages.FRIENDSHIP_REQUEST_NOT_PENDING + ": {}", friendship.getId());
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_REQUEST_NOT_PENDING);
        }
    }

    public void validateFriendshipRequest(long userId, long friendId, String userEmail) {
        if (friendshipRepository.friendshipExists(userId, friendId, FriendshipStatus.ACTIVE.getName())) {
            logger.error("Friendship  already exists for user: {} and friend: {}", userEmail, friendId);
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_ALREADY_ACTIVE);
        } else if (friendshipRepository.friendshipExists(userId, friendId, FriendshipStatus.BLOCKED.getName())) {
            logger.error("User : {} blocked by: {}", userEmail, friendId);
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_BLOCKED);
        } else  if (friendshipRepository.friendshipExists(userId, friendId, FriendshipStatus.PENDING.getName())) {
            logger.error("Friendship requests already exists for user: {} and friend: {}", userEmail, friendId);
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_ALREADY_PENDING);
        }
    }

    public void validatePendingSentFriendshipRequest(User user, Friendship friendship) {
        if (!friendship.getUser().getId().equals(user.getId())) {
            logger.error("User: {} doesn't match the user in friendship: {}", user.getId(), friendship.getId());
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_USER_DONT_MATCH);
        } else if (!friendship.getStatus().equals(FriendshipStatus.PENDING.getName())) {
            logger.error(Messages.FRIENDSHIP_REQUEST_NOT_PENDING + ": {}", friendship.getId());
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_REQUEST_NOT_PENDING);
        }
    }
}
