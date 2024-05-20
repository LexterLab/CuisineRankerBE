package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.friendship.Friendship;
import com.cranker.cranker.friendship.FriendshipDTO;
import com.cranker.cranker.friendship.FriendshipResponse;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
import com.cranker.cranker.token.payload.TokenDTO;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserController;
import com.cranker.cranker.user.UserService;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import com.cranker.cranker.user.payload.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
    }

    @AfterEach
    void tearDown() {
        reset(userService);
    }


    @Test
    public void shouldReturnUserInfoWhenGivenValidUsernameOrEmail() {
        UserDTO expectedUser = new UserDTO(1L,"Michael Myers", "michael@example.com", "URL",true, true);

        when(authentication.getName()).thenReturn("michael@example.com");
        when(userService.retrieveUserInfo("michael@example.com")).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = userController.getUserInfo(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    public void shouldReturnNotFoundWhenUserNotFoundByEmail() {
        String invalidEmail = "invalid@example.com";
        UserRequestDTO requestDTO = new UserRequestDTO("Alfred", "Hitch");

        when(authentication.getName()).thenReturn(invalidEmail);
        when(userService.retrieveUserInfo(invalidEmail)).thenThrow(ResourceNotFoundException.class);
        when(userService.changeUserPersonalInfo(invalidEmail, requestDTO)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userController.getUserInfo(authentication));
    }

    @Test
    public void shouldReturnAccessDeniedWhenNoUserSpecified() {
        UserRequestDTO requestDTO = new UserRequestDTO("Alfred", "Hitch");

        when(authentication.getName()).thenReturn(null);
        when(userService.retrieveUserInfo(null)).thenThrow(AccessDeniedException.class);
        when(userService.changeUserPersonalInfo(authentication.getName(), requestDTO))
                .thenThrow(AccessDeniedException.class);

        assertThrows(AccessDeniedException.class, () -> userController.getUserInfo(authentication));
        assertThrows(AccessDeniedException.class, () -> userController.changeUserPersonalInfo(authentication, requestDTO));
    }

    @Test
    void shouldRespondWithOKAndUpdatedUserPersonalInfoWhenProvidedValidEmail() {
        UserRequestDTO expectedUserInfo = new UserRequestDTO("Alfred", "Hitch");
        String email = "michael@example.com";


        when(userService.changeUserPersonalInfo(email, expectedUserInfo)).thenReturn(expectedUserInfo);
        when(authentication.getName()).thenReturn(email);

        ResponseEntity<UserRequestDTO> response = userController.changeUserPersonalInfo(authentication, expectedUserInfo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserInfo, response.getBody());
    }

    @Test
    void shouldRespondWithOKAndUserProfilePictures() {
        String email = "michael@example.com";
        PictureDTO picture = new PictureDTO(1L, "Rattingam", "url", "STARTER");
        List<PictureDTO> userPictures = List.of(picture);

        when(userService.retrieveUserProfilePictures(email)).thenReturn(userPictures);
        when(authentication.getName()).thenReturn(email);

        ResponseEntity<List<PictureDTO>> response = userController.getUserProfilePictures(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userPictures, response.getBody());
    }

    @Test
    void shouldChangeUserProfilePicture() {
        String email = "michael@example.com";
        UserDTO updatedUserInfo = new UserDTO(1L, "user user", "user@gmail.com", "newUrl",
                true, false);
        long pictureId = 1L;

        when(authentication.getName()).thenReturn(email);
        when(userService.changeUserProfilePicture(email, pictureId)).thenReturn(updatedUserInfo);

        ResponseEntity<UserDTO> response = userController.changeUserProfilePicture(authentication, pictureId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUserInfo, response.getBody());
    }

    @Test
    void shouldRespondWithOKAndUserFriendList() {
        String email = "michael@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        User friend = new User();
        friend.setId(2L);
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("ur;");
        friend.setSelectedPic(profilePicture);
        friend.setFirstName("Friend");
        friend.setLastName("Friend");

        Friendship friendship = new Friendship(1L, "Active", user, friend, LocalDateTime.now(), LocalDateTime.now());
        String updatedAtFormatted = friendship.getUpdatedAt().getDayOfMonth() + " " + DateTimeFormatter
                .ofPattern("MMMM").format(friendship.getUpdatedAt()) + " " + friendship.getUpdatedAt().getYear();

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, 2L, "Friend Friend", profilePicture.getUrl(),
                friendship.getStatus(), updatedAtFormatted, friendship.getCreatedAt(), friendship.getUpdatedAt());


        FriendshipResponse friendshipResponse = new FriendshipResponse(0, 10 , 1L,
                1, true, List.of(friendshipDTO));

        when(authentication.getName()).thenReturn(email);
        when(userService.retrieveUserFriends(email, 0, 10, "updatedAt", "asc"))
                .thenReturn(friendshipResponse);

        ResponseEntity<FriendshipResponse> response = userController
                .retrieveUserFriends(authentication, 0, 10, "updatedAt", "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendshipResponse, response.getBody());
    }
    @Test
    void shouldRespondWithCreatedStatusAndFriendshipDTOWhenRequestingFriendship() {
        String email = "michael@example.com";
        long friendId = 1L;

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, friendId,  "name", "image", "Pending",
                "20th April", LocalDateTime.now(), LocalDateTime.now());

        when(authentication.getName()).thenReturn(email);
        when(userService.sendFriendRequest(email, friendId)).thenReturn(friendshipDTO);

        ResponseEntity<FriendshipDTO> response = userController.requestFriendship(authentication, friendId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(friendshipDTO, response.getBody());
    }

    @Test
    void shouldRespondWithUserFriendshipRequestsAndOKStatus() {
        String email = "michael@example.com";

        long friendId = 1L;

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, friendId,  "name", "image", "Pending",
                "20th April", LocalDateTime.now(), LocalDateTime.now());

        FriendshipResponse friendshipResponse = new FriendshipResponse(0, 10,
                1L, 1, true, List.of(friendshipDTO) );

        when(authentication.getName()).thenReturn(email);
        when(userService.retrieveUserFriendshipRequests(email, 0, 10,
                "updatedAt",  "asc")).thenReturn(friendshipResponse);

        ResponseEntity<FriendshipResponse> response = userController.retrieveUserFriendshipRequests(authentication,0, 10,
                "updatedAt",  "asc" );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendshipResponse, response.getBody());
    }

    @Test
    void shouldRespondWithUserSentFriendshipRequestsAndOKStatus() {
        String email = "michael@example.com";
        long friendId = 1L;

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, friendId,  "name", "image", "Pending",
                "20th April", LocalDateTime.now(), LocalDateTime.now());

        FriendshipResponse friendshipResponse = new FriendshipResponse(0, 10,
                1L, 1, true, List.of(friendshipDTO) );

        when(authentication.getName()).thenReturn(email);
        when(userService.retrieveUserSentFriendshipRequests(email, 0, 10,
                "updatedAt",  "asc")).thenReturn(friendshipResponse);

        ResponseEntity<FriendshipResponse> response = userController.retrieveUserSentFriendshipRequests(authentication, 0, 10,
                "updatedAt",  "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendshipResponse, response.getBody());

    }

    @Test
    void shouldRespondWithAcceptedFriendshipAndOKStatus() {
        String email = "michael@example.com";
        long friendId = 1L;
        long friendshipId = 2L;

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, friendId,  "name", "image", "Active",
                "20th April", LocalDateTime.now(), LocalDateTime.now());

        when(authentication.getName()).thenReturn(email);
        when(userService.acceptFriendRequest(email, friendshipId)).thenReturn(friendshipDTO);

        ResponseEntity<FriendshipDTO> response = userController.acceptFriendship(authentication, friendshipId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendshipDTO, response.getBody());
    }

    @Test
    void shouldRespondWithNoContentStatusWhenRejectingFriendship() {
        String email = "michael@example.com";
        long friendshipId = 1L;

        when(authentication.getName()).thenReturn(email);
        doNothing().when(userService).rejectFriendRequest(email, friendshipId);

        ResponseEntity<Void> response = userController.rejectFriendship(authentication, friendshipId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldRespondWithAvailableUsersAndOKStatus() {
        String email = "michael@example.com";
        String query = "user";

        UserDTO userDTO = new UserDTO(1L, "user", email, "picture", true,
                true);
        UserResponse userResponse = new UserResponse(0, 10,
                1L, 1, true, List.of(userDTO));

        when(authentication.getName()).thenReturn(email);
        when(userService.searchUsers(email, query,0, 10,
                "updatedAt",  "asc")).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.searchUsers(authentication, query,0, 10,
                "updatedAt",  "asc");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponse, response.getBody());
    }

    @Test
    void shouldRespondWithGeneratedFriendshipTokenAndCreatedStatus() {
        String email = "michael@example.com";
        TokenDTO tokenDTO = new TokenDTO("token");

        when(authentication.getName()).thenReturn(email);
        when(userService.generateFriendshipToken(email)).thenReturn(tokenDTO);

        ResponseEntity<TokenDTO> response = userController.generateFriendshipToken(authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tokenDTO, response.getBody());
    }

    @Test
    void shouldRespondWithActivatedFriendshipAndOKStatusWhenActivatingFriendshipToken() {
        String email = "michael@example.com";
        long friendId = 1L;

        TokenDTO tokenDTO = new TokenDTO("token");

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, friendId,  "name", "image", "Active",
                "20th April", LocalDateTime.now(), LocalDateTime.now());

        when(authentication.getName()).thenReturn(email);
        when(userService.addFriendViaToken(email, tokenDTO)).thenReturn(friendshipDTO);

        ResponseEntity<FriendshipDTO> response = userController.activateFriendshipToken(authentication, tokenDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(friendshipDTO, response.getBody());
    }

    @Test
    void shouldRespondWithNoContentStatusWhenRejectingPendingFriendship() {
        String email = "michael@example.com";
        long friendshipId = 1L;

        when(authentication.getName()).thenReturn(email);
        doNothing().when(userService).cancelFriendshipRequest(email, friendshipId);

        ResponseEntity<Void> response = userController.cancelSentFriendshipRequest(authentication, friendshipId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}