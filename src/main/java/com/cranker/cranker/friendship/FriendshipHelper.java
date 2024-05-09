package com.cranker.cranker.friendship;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.user.User;
import com.cranker.cranker.utils.Messages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FriendshipHelper {
    private final Logger logger = LogManager.getLogger(this);

    public void validatePendingFriendshipRequest(User user, Friendship friendship, Long friendshipId) {
        if (!friendship.getFriend().getId().equals(user.getId())) {
            logger.error("User: {} doesn't match the user in friendship: {}", user.getId(), friendshipId);
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_USER_DONT_MATCH);
        } else if (!friendship.getStatus().equals(FriendshipStatus.PENDING.getName())) {
            logger.error(Messages.FRIENDSHIP_REQUEST_NOT_PENDING + ": {}", friendship.getId());
            throw new APIException(HttpStatus.CONFLICT, Messages.FRIENDSHIP_REQUEST_NOT_PENDING);
        }
    }
}
