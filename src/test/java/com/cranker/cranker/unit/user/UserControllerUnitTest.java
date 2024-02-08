package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.user.UserController;
import com.cranker.cranker.user.UserDTO;
import com.cranker.cranker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test
    public void shouldReturnUserInfoWhenGivenValidUsernameOrEmail() {
        UserDTO expectedUser = new UserDTO(1L,"Michael Myers", "michael@example.com", true);

        when(authentication.getName()).thenReturn("michael@example.com");
        when(userService.retrieveUserInfo("michael@example.com")).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = userController.getUserInfo(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
    }

    @Test
    public void shouldReturnNotFoundWhenUserNotFoundByEmail() {
        String invalidEmail = "invalid@example.com";

        when(authentication.getName()).thenReturn(invalidEmail);
        when(userService.retrieveUserInfo(invalidEmail)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userController.getUserInfo(authentication));
    }

    @Test
    public void shouldReturnAccessDeniedWhenNoUserSpecified() {
        when(authentication.getName()).thenReturn(null);
        when(userService.retrieveUserInfo(null)).thenThrow(AccessDeniedException.class);

        assertThrows(AccessDeniedException.class, () -> userController.getUserInfo(authentication));
    }
}