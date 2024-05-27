package com.cranker.cranker.authentication;

import com.cranker.cranker.authentication.payload.SignUpRequestDTO;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
import com.cranker.cranker.role.Role;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthenticationHelper {
    private final RoleRepository roleRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final PasswordEncoder encoder;

    public User setRoles(User user) {
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
        Role role = new Role();
        if (userRole.isPresent()) {
            role = userRole.get();
        }
        user.setRoles(Set.of(role));
        return user;
    }
    public void setPictures(User user) {
        List<ProfilePicture> starterPictures = profilePictureRepository.findAllByCategoryName("STARTER");
        user.setProfilePictures(starterPictures);
        Optional<ProfilePicture> defaultProfilePic = profilePictureRepository.findByNameIgnoreCase("Rattingam");
        defaultProfilePic.ifPresent(user::setSelectedPic);
    }
    public User buildUser(SignUpRequestDTO requestDTO) {
        User user = new User();
        user.setFirstName(requestDTO.firstName());
        user.setLastName(requestDTO.lastName());
        user.setPassword(encoder.encode(requestDTO.password()));
        user.setEmail(requestDTO.email());
        setPictures(user);
        return setRoles(user);
    }
}
