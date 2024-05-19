package com.cranker.cranker.user;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.friendship.*;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
import com.cranker.cranker.profile_pic.payload.PictureMapper;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
import com.cranker.cranker.token.TokenService;
import com.cranker.cranker.token.payload.TokenDTO;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import com.cranker.cranker.user.payload.UserResponse;
import com.cranker.cranker.utils.Messages;
import com.cranker.cranker.utils.PageableUtil;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfilePictureRepository pictureRepository;
    private final Logger logger = LogManager.getLogger(this);
    private final FriendshipRepository friendshipRepository;
    private final FriendshipHelper friendshipHelper;
    private final PageableUtil pageableUtil;
    private final TokenService friendshipTokenService;

    public UserDTO retrieveUserInfo(String email) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.info("Retrieving user info for User: {}", user.getEmail());
        return UserResponseMapper.INSTANCE.entityToDTO(user);
    }

    public UserRequestDTO changeUserPersonalInfo(String email, UserRequestDTO requestDTO) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserResponseMapper.INSTANCE.updateUserFromDto(requestDTO, user);
        logger.info("Updating User's personal info for User: {}", user.getEmail());
        return UserResponseMapper.INSTANCE.entityToRequestDTO(userRepository.save(user));
    }

    public List<PictureDTO> retrieveUserProfilePictures(String email) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.info("Retrieving user profile pictures for: {}", email);
        return PictureMapper.INSTANCE.entityToDTO(user.getProfilePictures());
    }

    public UserDTO changeUserProfilePicture(String email, Long pictureId) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        ProfilePicture picture = pictureRepository.findById(pictureId)
                .orElseThrow(() -> new ResourceNotFoundException("Picture", "Id", pictureId));

        user.setSelectedPic(picture);
        logger.info("Set  profile picture: {} for user: {}", picture.getName(),  email);
        return UserResponseMapper.INSTANCE.entityToDTO(userRepository.save(user));
    }

    public FriendshipResponse retrieveUserFriends(String email, int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Friendship> friendshipPage = friendshipRepository.findAllFriendsByUserId(user.getId(), pageable);

        List<FriendshipDTO> friendships = new ArrayList<>();

        for (Friendship friendship : friendshipPage) {
            if (friendship.getUser().getId().equals(user.getId())) {
                friendships.add(FriendshipMapper.INSTANCE.friendshipToFriendshipDTOUserVersion(friendship));
            }  else {
                friendships.add(FriendshipMapper.INSTANCE.friendshipToFriendshipDTOFriendVersion(friendship));
            }
        }

        logger.info("Retrieving friends for: {}", email);
        return FriendshipMapper.INSTANCE.pageToFriendshipResponse(friendshipPage, friendships);
    }

    @Transactional
    public FriendshipDTO sendFriendRequest(String userEmail, Long friendId) {
        User user = userRepository.findUserByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend", "Id", friendId));


        friendshipHelper.validateFriendshipRequest(user.getId(), friendId, userEmail);

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);

        logger.info("Sending friend request: {} to: {}", userEmail, friendId);

        return FriendshipMapper.INSTANCE.friendshipToFriendshipDTOUserVersion(friendshipRepository.save(friendship));
    }

    public FriendshipResponse retrieveUserFriendshipRequests(String email, int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Friendship> friendshipPage = friendshipRepository.findAllFriendRequests(user.getId(), pageable);

        List<FriendshipDTO> friendships = new ArrayList<>();

        for (Friendship friendship : friendshipPage) {
            friendships.add(FriendshipMapper.INSTANCE.friendshipToFriendshipDTOFriendVersion(friendship));
        }

        logger.info("Retrieving friendship requests for: {}", email);
        return FriendshipMapper.INSTANCE.pageToFriendshipResponse(friendshipPage, friendships);
    }

    public FriendshipResponse retrieveUserSentFriendshipRequests(String email, int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, sortDir);
        Page<Friendship> friendshipPage = friendshipRepository.findAllSentFriendRequests(user.getId(), pageable);

        List<FriendshipDTO> friendships = new ArrayList<>();

        for (Friendship friendship : friendshipPage) {
            friendships.add(FriendshipMapper.INSTANCE.friendshipToFriendshipDTOUserVersion(friendship));
        }

        logger.info("Retrieving sent friendship requests for: {}", email);
        return FriendshipMapper.INSTANCE.pageToFriendshipResponse(friendshipPage, friendships);
    }

    @Transactional
    public FriendshipDTO acceptFriendRequest(String email, Long friendshipId) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship", "Id", friendshipId));

        friendshipHelper.validatePendingFriendshipRequest(user, friendship, friendshipId);

        friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);
        logger.info("Accepting friendship request : {} from: {}", friendshipId, friendship.getUser().getEmail());
        return FriendshipMapper.INSTANCE.friendshipToFriendshipDTOFriendVersion(friendshipRepository.save(friendship));
    }

    @Transactional
    public void rejectFriendRequest(String email, Long friendshipId) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship", "Id", friendshipId));

        friendshipHelper.validatePendingFriendshipRequest(user, friendship, friendshipId);

        friendshipRepository.delete(friendship);
        logger.info("User: {} rejected friendship request: {}", user.getId(), friendshipId);
    }

    public UserResponse searchUsers(String email, String query, int pageNo, int pageSize, String sortBy, String sortDir) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Pageable pageable = pageableUtil.getPageable(pageNo, pageSize, sortBy, sortDir);

        Page<User> userPage = userRepository.findAllByNameAndNotFriends(query, user, pageable);

        logger.info("Searching users for: {}", email);
        return UserResponseMapper.INSTANCE.pageToUserResponse(userPage, UserResponseMapper.INSTANCE
                .entityToDTO(userPage.getContent()));
    }

   @Transactional
   public TokenDTO generateFriendshipToken(String email) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        String friendshipToken = friendshipTokenService.generateToken(user);

        return new TokenDTO(friendshipToken);
    }

    @Transactional
    public FriendshipDTO addFriendViaToken(String email, TokenDTO tokenDTO) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        friendshipTokenService.confirmToken(tokenDTO.value());

        User friend = friendshipTokenService.getUserByToken(tokenDTO.value());

        if (friend.getId().equals(user.getId())) {
            logger.error(Messages.ADDING_YOURSELF_AS_FRIEND + "{}, {}", user.getId(), friend.getId());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.ADDING_YOURSELF_AS_FRIEND);
        }

        long friendId = friend.getId();

        if (friendshipRepository.friendshipExists(user.getId(), friendId, FriendshipStatus.PENDING.getName())) {
            Friendship friendship = friendshipRepository.findByUserIdAndFriendId(user.getId(), friendId)
                    .orElseThrow(() -> new ResourceNotFoundException("Friendship", "Id", friendId));
            friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);
            logger.info("Pending friend request detected {}, changed to active", friendship.getId());
            return FriendshipMapper.INSTANCE.friendshipToFriendshipDTOUserVersion(friendshipRepository.save(friendship));
        }
        friendshipHelper.validateFriendshipRequest(user.getId(), friendId, email);

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);

        logger.info("Successfully activated friendship with token: {}", tokenDTO.value());
        return FriendshipMapper.INSTANCE.friendshipToFriendshipDTOUserVersion(friendshipRepository.save(friendship));
    }

    @Transactional
    public void cancelFriendshipRequest(String email, Long friendshipId) {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Friendship", "Id", friendshipId));

        friendshipHelper.validatePendingSentFriendshipRequest(user, friendship);

        friendshipRepository.delete(friendship);
        logger.info("User: {} cancelled friendship request: {}", user.getId(), friendshipId);
    }


}
