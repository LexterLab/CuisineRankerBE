package com.cranker.cranker.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
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

    public UserRequestDTO changeUserPersonalInfo(String email, UserRequestDTO requestDTO) {
        User user = repository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserResponseMapper.INSTANCE.updateUserFromDto(requestDTO, user);
        user.setFirstName(requestDTO.firstName());
        return UserResponseMapper.INSTANCE.entityToRequestDTO(repository.save(user));
    }
}
