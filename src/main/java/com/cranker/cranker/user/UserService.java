package com.cranker.cranker.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.profile_pic.payload.PictureMapper;
import com.cranker.cranker.profile_pic.payload.PicturesDTO;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final Logger logger = LogManager.getLogger(this);
    public UserDTO retrieveUserInfo(String email) {
        User user = repository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.info("Retrieving user info for User: {}", user.getEmail());
        return UserResponseMapper.INSTANCE.entityToDTO(user);
    }

    public UserRequestDTO changeUserPersonalInfo(String email, UserRequestDTO requestDTO) {
        User user = repository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserResponseMapper.INSTANCE.updateUserFromDto(requestDTO, user);
        user.setFirstName(requestDTO.firstName());
        logger.info("Updating User's personal info for User: {}", user.getEmail());
        return UserResponseMapper.INSTANCE.entityToRequestDTO(repository.save(user));
    }

    public List<PicturesDTO> retrieveUserProfilePictures(String email) {
        User user = repository.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        logger.info("Retrieving user profile pictures for: {}", email);
        return PictureMapper.INSTANCE.entityToDTO(user.getProfilePictures());
    }
}
