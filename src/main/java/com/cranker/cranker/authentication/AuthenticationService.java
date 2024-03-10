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
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
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

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthenticationHelper helper;
    private final TokenService emailConfirmationTokenService;
    private final TokenService resetPasswordTokenService;
    private final EmailService emailService;
    private final Properties properties;
    private final PasswordEncoder encoder;
    private final Logger logger = LogManager.getLogger(this);

    public JWTAuthenticationResponse login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        logger.info("{} authenticated successfully", loginRequestDTO.email());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JWTAuthenticationResponse authenticationResponse = new JWTAuthenticationResponse();
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
}