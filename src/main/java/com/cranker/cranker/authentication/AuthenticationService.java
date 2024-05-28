package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.authentication.jwt.JwtRefreshRequestDTO;
import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.authentication.jwt.JwtType;
import com.cranker.cranker.authentication.payload.*;
import com.cranker.cranker.email.EmailService;
import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.TokenService;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.utils.Messages;
import com.cranker.cranker.utils.Properties;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthenticationHelper helper;
    private final TokenService emailConfirmationTokenService;
    private final TokenService resetPasswordTokenService;
    private final TokenService changeEmailTokenService;
    private final TokenService twoFactorTokenService;
    private final EmailService emailService;
    private final Properties properties;
    private final PasswordEncoder encoder;
    private final Logger logger = LogManager.getLogger(this);

    @Transactional
    public JWTAuthenticationResponse login(LoginRequestDTO loginRequestDTO) throws MessagingException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        logger.info("{} authenticated successfully", loginRequestDTO.email());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JWTAuthenticationResponse authenticationResponse = new JWTAuthenticationResponse();

        User user = userRepository.findUserByEmailIgnoreCase(loginRequestDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", loginRequestDTO.email()));

        if (user.getIsTwoFactorEnabled()) {
            logger.info(Messages.ENABLED_2FA + ": {}", loginRequestDTO.email());
            String token = twoFactorTokenService.generateToken(user);
            emailService.sendTwoFactorEmail(user, token);
            authenticationResponse.setIsTwoFactor(true);
        }

        authenticationResponse.setAccessToken(tokenProvider.generateToken(authentication.getName(), JwtType.ACCESS));
        authenticationResponse.setRefreshToken(tokenProvider.generateToken(authentication.getName(), JwtType.REFRESH));
        return authenticationResponse;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Transactional
    public String signUp(SignUpRequestDTO requestDTO) throws MessagingException {
        if (userRepository.existsByEmailIgnoreCase(requestDTO.email())) {
            logger.error(Messages.EMAIL_EXISTS + ": {}", requestDTO.email());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_EXISTS);
        }

        User user = helper.buildUser(requestDTO);
        userRepository.save(user);
        logger.info(Messages.USER_SUCCESSFULLY_REGISTERED + ": {}", user.getEmail());
        String code = emailConfirmationTokenService.generateToken(user);
        String confirmationURL = properties.getEmailURL() + code;
        emailService.sendConfirmationEmail(user, confirmationURL, code);
        logger.info("Account confirmation email successfully sent to: {}", user.getEmail());
        return Messages.USER_SUCCESSFULLY_REGISTERED;
    }

    @Transactional
    public void confirmEmail(String value) {
        emailConfirmationTokenService.confirmToken(value);
    }

    @Transactional
    public void resetPassword(String value, ResetPasswordRequestDTO requestDTO) {
        User user = resetPasswordTokenService.getUserByToken(value);

        resetPasswordTokenService.confirmToken(value);
        logger.info("Reset Password Token Confirmed: {}", value);
        user.setPassword(encoder.encode(requestDTO.newPassword()));
        userRepository.save(user);
        logger.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO requestDTO) throws MessagingException {
        User user = userRepository.findUserByEmailIgnoreCase(requestDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", requestDTO.email()));

        String resetURL = properties.getResetURL() + resetPasswordTokenService.generateToken(user);
        emailService.sendResetPasswordEmail(user, resetURL);
        logger.info("Reset Password email successfully sent to: {}", user.getEmail());
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO requestDTO, String email) throws MessagingException {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email",email));

        if(!encoder.matches(requestDTO.oldPassword(), user.getPassword())) {
            logger.error("User {} provided incorrect old password", user.getEmail());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.OLD_PASSWORD_WRONG);
        }

        if(requestDTO.oldPassword().equals(requestDTO.newPassword())) {
            logger.error("User {} provided identical new password", user.getEmail());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.PASSWORD_NOT_CHANGED);
        }

        user.setPassword(encoder.encode(requestDTO.newPassword()));
        userRepository.save(user);
        logger.info("Password successfully changed for {}", user.getEmail());
        emailService.sendChangedPasswordEmail(user);
        logger.info("Changed Password email confirmation successfully sent to: {}", user.getEmail());
    }

    @Transactional
    public JWTAuthenticationResponse refreshToken(JwtRefreshRequestDTO requestDTO) {
        tokenProvider.validateToken(requestDTO.refreshToken());
        String email = tokenProvider.getUsername(requestDTO.refreshToken());
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));
        JWTAuthenticationResponse response = new JWTAuthenticationResponse();
        response.setRefreshToken(requestDTO.refreshToken());
        response.setAccessToken(tokenProvider.generateToken(user.getEmail(), JwtType.ACCESS));
        logger.info("Refreshed access token for User: {}", user.getEmail());
        return response;
    }

    public void requestChangeUserEmail(ChangeEmailRequestDTO requestDTO, String email) throws MessagingException {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

        if (!user.getEmail().equals(requestDTO.oldEmail())) {
            logger.error("User {} provided incorrect old email", email);
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.OLD_EMAIL_WRONG);
        }

        if (requestDTO.oldEmail().equals(requestDTO.newEmail())) {
            logger.error("User {} provided same email twice", email);
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_NOT_CHANGED);
        }

        if (userRepository.existsByEmailIgnoreCase(requestDTO.newEmail())) {
            logger.error(Messages.EMAIL_EXISTS + ": {}", requestDTO.newEmail());
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_EXISTS);
        }

        user.setEmail(requestDTO.newEmail());
        String changeEmailURL = properties.getChangeEmailURL() + changeEmailTokenService.generateToken(user);
        emailService.sendChangeEmailRequestEmail(user, changeEmailURL,  email);
        logger.info("Change Email Request Email successfully sent to: {}", email);
    }

   @Transactional
   public void changeUserEmail(String email, String value) throws MessagingException {
       User user = userRepository.findUserByEmailIgnoreCase(email)
               .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

       changeEmailTokenService.confirmToken(value);
       logger.info("Token confirmed and validated: {}", value);

       String newEmail = tokenProvider.getUsername(value);
       user.setEmail(newEmail);
       userRepository.save(user);
       logger.info("Successfully changed email for user: {}", email);

       emailService.sendChangedEmailEmail(List.of(user.getEmail(), email).toArray(new String[0]), user.getFirstName());
       logger.info("Successfully sent email to notify email changing to new address: {}", newEmail);
       logger.info("Successfully sent email to notify email changing to old address: {}", email);
   }

   @Transactional
   public void changeTwoFactorAuthenticationMode(String email) throws MessagingException {
       User user = userRepository.findUserByEmailIgnoreCase(email)
               .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

       if (!user.getIsVerified()) {
           logger.error(Messages.NOT_VERIFIED + ": {}", email);
           throw new APIException(HttpStatus.BAD_REQUEST, Messages.NOT_VERIFIED);
       }

       userRepository.switchTwoFactorAuth(email, !user.getIsTwoFactorEnabled());
       logger.info("User: {} set 2FA to: {}", email, !user.getIsTwoFactorEnabled());

       emailService.sendTwoFactorStatusEmail(user);
       logger.info("Sent successfully email to notify user of switching 2FA mode");
   }

    @Transactional
    public void confirmTwoFactorAuthentication(TwoFactorRequestDTO requestDTO, String email) {

        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

        User tokenUser = twoFactorTokenService.getUserByToken(requestDTO.token());

        if (!user.getEmail().equals(tokenUser.getEmail())) {
            logger.error(Messages.TOKEN_DONT_MATCH_USER + ": {}", email);
            throw new APIException(HttpStatus.UNAUTHORIZED, Messages.TOKEN_DONT_MATCH_USER);
        }

        twoFactorTokenService.confirmToken(requestDTO.token());
        logger.info("Successfully confirmed 2FA token for user: {}", email);
    }

    @Transactional
    public void requestResendConfirmationEmail(String email) throws MessagingException {
        User user = userRepository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

        if (user.getIsVerified()) {
            logger.error(Messages.EMAIL_ALREADY_CONFIRMED + ": {}", email);
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_ALREADY_CONFIRMED);
        }

        String code = emailConfirmationTokenService.generateToken(user);
        String confirmationURL = properties.getEmailURL() + code;
        emailService.sendEmailConfirmationResend(user, confirmationURL);
        logger.info("Account confirmation email successfully resent to: {}", user.getEmail());
    }
}