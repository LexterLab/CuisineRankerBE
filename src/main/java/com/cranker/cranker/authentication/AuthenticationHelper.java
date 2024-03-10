package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.payload.SignUpRequestDTO;
import com.cranker.cranker.role.Role;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {
    private final RoleRepository repository;
    private final PasswordEncoder encoder;

    private User setRoles(User user) {
        Optional<Role> userRole = repository.findByName("ROLE_USER");
        Role role = new Role();
        if (userRole.isPresent()) {
            role = userRole.get();
        }
        user.setRoles(Set.of(role));
        return user;
    }

    public User buildUser(SignUpRequestDTO requestDTO) {
        User user = new User();
        user.setFirstName(requestDTO.firstName());
        user.setLastName(requestDTO.lastName());
        user.setPassword(encoder.encode(requestDTO.password()));
        user.setEmail(requestDTO.email());
        return setRoles(user);
    }
}
