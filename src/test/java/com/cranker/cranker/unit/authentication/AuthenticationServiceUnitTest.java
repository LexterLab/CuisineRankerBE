package com.cranker.cranker.unit.authentication;

import com.cranker.cranker.authentication.AuthenticationHelper;
import com.cranker.cranker.authentication.AuthenticationService;
import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.authentication.jwt.JwtType;
import com.cranker.cranker.authentication.payload.*;
import com.cranker.cranker.email.EmailService;
import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.role.Role;
import com.cranker.cranker.token.Token;
import com.cranker.cranker.token.TokenService;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.utils.Messages;
import com.cranker.cranker.utils.Properties;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private Properties properties;

    @Mock
    private User user;
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;


    @AfterEach
    void tearDown() {
        reset(userRepository, passwordEncoder, emailService, tokenService,passwordEncoder,
                authenticationHelper, properties);
    }
    @Test
    void shouldThrowBadCredentialsExceptionWhenProvidedInvalidCredentials() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("invalid@example.com", "password");

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(),
                loginRequestDTO.password()))).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(loginRequestDTO));
    }


    @Test
    void shouldReturnAccessTokenAndRefreshTokenWhenSignedIn() {
        Authentication authentication = mock(Authentication.class);
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("valid@example.com", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication.getName(), JwtType.ACCESS)).thenReturn("access_token");
        when(tokenProvider.generateToken(authentication.getName(), JwtType.REFRESH)).thenReturn("refresh_token");

        JWTAuthenticationResponse response = authenticationService.login(loginRequestDTO);

        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
    }

    @Test
    void shouldThrowAPIExceptionWhenProvidedExistingEmail() throws MessagingException {
        String existingEmail = "existing@gmail.com";
        SignUpRequestDTO requestDTO = new SignUpRequestDTO("validName", "validName", "password",
                "password", existingEmail);

        when(userRepository.existsByEmailIgnoreCase(existingEmail)).thenThrow(APIException.class);

        assertThrows(APIException.class, () -> authenticationService.signUp(requestDTO));
    }
    @Test
    void shouldReturnSuccessFullMessageWhenProvidedValidSignUpCredentials() throws MessagingException {
        SignUpRequestDTO requestDTO = new SignUpRequestDTO
                (
                        "FirstName", "LastName", "password", "password"
                        , "email@example.com"
                );

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        User newUser = new User();
        newUser.setFirstName(requestDTO.firstName());
        newUser.setLastName(requestDTO.lastName());
        newUser.setRoles(Set.of(userRole));
        newUser.setEmail(requestDTO.email());
        newUser.setPassword(requestDTO.password());

        when(authenticationHelper.buildUser(requestDTO)).thenReturn(newUser);
        when(userRepository.existsByEmailIgnoreCase(requestDTO.email())).thenReturn(false);

        String message = authenticationService.signUp(requestDTO);

        verify(userRepository).save(newUser);
        assertEquals(Messages.USER_SUCCESSFULLY_REGISTERED, message);
    }

    @Test
    void shouldThrowResourceNotFoundWhenProvidedInvalidEmailForForgotPassword() {
        String recipient = "notfound@example.com";
        ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO(recipient);

        when(userRepository.findUserByEmailIgnoreCase(recipient)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> authenticationService.forgotPassword(requestDTO));
    }

    @Test
    void shouldResetPassword() {
        String tokenValue = "validToken";
        String newPassword = "newSecurePassword";

        user = new User();
        user.setPassword(newPassword);
        user.setEmail("email@gmail.com");
        user.setId(1L);

        Token token = new Token();
        token.setValue(tokenValue);
        token.setUserId(user.getId());


        when(tokenService.getUserByToken(tokenValue)).thenReturn(user);
        doNothing().when(tokenService).confirmToken(tokenValue);
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);


        authenticationService.resetPassword(tokenValue, new ResetPasswordRequestDTO(newPassword, newPassword));


        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldConfirmEmail() {
        String value = "verification token";
        authenticationService.confirmEmail(value);

        verify((tokenService)).confirmToken(value);
    }

    @Test
    void shouldUpdateUserPassword() throws MessagingException {
        String email = "user@gmail.com";
        String oldPassword = "oldPassword";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);


        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO(oldPassword, "newPass",
                "newPass");

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(emailService).sendChangedPasswordEmail(user);

        authenticationService.changePassword(requestDTO, email);

        verify(emailService).sendChangedPasswordEmail(user);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowNotFoundWhenNoUserExistByEmailWhenChangingPassword() {
        String email = "user@gmail.com";
        String oldPassword = "oldPassword";
        User user = new User();
        user.setEmail(email);

        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO(oldPassword, "newPass",
                "newPass");

        when(userRepository.findUserByEmailIgnoreCase(email)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> authenticationService.changePassword(requestDTO, email));
    }

    @Test
    void shouldThrowAPIExceptionWhenOldPasswordIsIncorrect() throws MessagingException {
        String email = "user@gmail.com";
        String oldPassword = "oldPassword";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO(oldPassword, "newPass",
                "newPass");

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(emailService).sendChangedPasswordEmail(user);

        assertThrows(APIException.class, () -> authenticationService.changePassword(requestDTO, email));
    }

    @Test
    void shouldThrowAPIExceptionWhenProvidedSamePasswordAsOldOne() throws MessagingException {
        String email = "user@gmail.com";
        String oldPassword = "newPass";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);


        ChangePasswordRequestDTO requestDTO = new ChangePasswordRequestDTO(oldPassword, "newPass",
                "newPass");

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, encodedPassword)).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        doNothing().when(emailService).sendChangedPasswordEmail(user);


        assertThrows(APIException.class, () -> authenticationService.changePassword(requestDTO, email));
    }



}
