package com.cranker.cranker.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserDTO retrieveUserInfo(String email) {
        User user = repository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return UserResponseMapper.INSTANCE.entityToDTO(user);
    }
}
