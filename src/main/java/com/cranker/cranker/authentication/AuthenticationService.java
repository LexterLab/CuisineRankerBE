package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.authentication.jwt.JwtTokenProvider;
import com.cranker.cranker.authentication.jwt.JwtType;
import com.cranker.cranker.authentication.payload.ForgotPasswordRequestDTO;
import com.cranker.cranker.authentication.payload.LoginRequestDTO;
import com.cranker.cranker.authentication.payload.ResetPasswordRequestDTO;
import com.cranker.cranker.authentication.payload.SignUpRequestDTO;
import com.cranker.cranker.email.EmailService;
import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.token.TokenService;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.utils.Messages;
import com.cranker.cranker.utils.Properties;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
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

    public JWTAuthenticationResponse login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
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
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_EXISTS);
        }

        User user = helper.buildUser(requestDTO);
        userRepository.save(user);
        String code = emailConfirmationTokenService.generateToken(user);
        String confirmationURL = properties.getEmailURL() + code;
        emailService.sendConfirmationEmail(user, confirmationURL, code);
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

        user.setPassword(encoder.encode(requestDTO.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO requestDTO) throws MessagingException {
        User user = userRepository.findUserByEmailIgnoreCase(requestDTO.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "Email", requestDTO.email()));

        String resetURL = properties.getResetURL() + resetPasswordTokenService.generateToken(user);
        emailService.sendResetPasswordEmail(user, resetURL);
    }
}