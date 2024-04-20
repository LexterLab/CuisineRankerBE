package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.friendship.Friendship;
import com.cranker.cranker.friendship.FriendshipDTO;
import com.cranker.cranker.friendship.FriendshipResponse;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserController;
import com.cranker.cranker.user.UserService;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
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
}