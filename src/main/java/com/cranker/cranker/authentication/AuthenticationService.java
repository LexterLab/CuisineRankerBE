package com.cranker.cranker.authentication;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.jwt.JWTAuthenticationResponse;
import com.cranker.cranker.jwt.JwtTokenProvider;
import com.cranker.cranker.jwt.TokenType;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.utils.Messages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthenticationHelper helper;

    public JWTAuthenticationResponse login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JWTAuthenticationResponse authenticationResponse = new JWTAuthenticationResponse();
        authenticationResponse.setAccessToken(tokenProvider.generateToken(authentication.getName(), TokenType.ACCESS));
        authenticationResponse.setRefreshToken(tokenProvider.generateToken(authentication.getName(), TokenType.REFRESH));
        return authenticationResponse;
    }

    public String logout() {
        SecurityContextHolder.clearContext();
        return Messages.SUCCESSFUL_LOGOUT;
    }

    @Transactional
    public String signUp(SignUpRequestDTO requestDTO){
        if (userRepository.existsByEmailIgnoreCase(requestDTO.email())) {
            throw new APIException(HttpStatus.BAD_REQUEST, Messages.EMAIL_EXISTS);
        }

        User user = helper.buildUser(requestDTO);
        userRepository.save(user);
        return Messages.USER_SUCCESSFULLY_REGISTERED;
    }
}
