package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.user.UserService;
import com.cranker.cranker.user.payload.UserRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

//    @AfterEach
//    void tearDown() {
//        reset(repository);
//    }


    @Test
    public void shouldReturnUserInfoWhenGivenValidEmail() {
        String email = "michael@example.com";
        User expectedUserInfo = new User();
        expectedUserInfo.setEmail(email);

        when(repository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUserInfo));

        UserDTO result = service.retrieveUserInfo(email);

        assertEquals(email, result.email());
    }

    @Test
    public void shouldThrowResourceNotFoundWhenGivenInvalidEmail() {
        String invalidEmail = "george@example.com";

        UserRequestDTO requestDTO = new UserRequestDTO("Alfred", "Hitch");

        when(repository.findUserByEmailIgnoreCase(invalidEmail)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.retrieveUserInfo(invalidEmail));
        assertThrows(ResourceNotFoundException.class, () -> service.changeUserPersonalInfo(invalidEmail, requestDTO));
    }

    @Test
     void shouldChangeUserPersonalInfoWhenProvidedValidEmail() {
        UserRequestDTO expectedUserInfo = new UserRequestDTO("Alfred", "Hitch");
        String email = "michael@example.com";
        User user = new User();


        when(repository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        UserRequestDTO changedUserInfo = service.changeUserPersonalInfo(email, expectedUserInfo);

        assertEquals(expectedUserInfo, changedUserInfo);
    }



}
