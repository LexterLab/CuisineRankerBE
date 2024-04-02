package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
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
}