package com.cranker.cranker.unit.authentication;

import com.cranker.cranker.authentication.AuthenticationHelper;
import com.cranker.cranker.authentication.payload.SignUpRequestDTO;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
import com.cranker.cranker.role.Role;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationHelperUnitTest {
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProfilePictureRepository pictureRepository;
    @InjectMocks
    private AuthenticationHelper authenticationHelper;


    @BeforeEach
    void setUp() {
        reset(roleRepository, passwordEncoder);
    }
    @AfterEach
    void tearDown() {
        reset(roleRepository, passwordEncoder);
    }

    @Test
    void shouldSetUserRoleToUserIfRolePresent() {
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        Set<Role> roles = Set.of(userRole);
        SignUpRequestDTO requestDTO = new SignUpRequestDTO("User", "User", "password123",
                "password123", "user@example.com");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        User updatedUser = authenticationHelper.buildUser(requestDTO);

        assertEquals(roles, updatedUser.getRoles());
    }

    @Test
    void shouldSetDefaultProfilePic() {
        ProfilePicture profilePicture = new ProfilePicture();

        SignUpRequestDTO requestDTO = new SignUpRequestDTO("User", "User", "password123",
                "password123", "user@example.com");

        when(pictureRepository.findByNameIgnoreCase("Rattingam")).thenReturn(Optional.of(profilePicture));

        User updatedUser = authenticationHelper.buildUser(requestDTO);

        assertEquals(profilePicture, updatedUser.getSelectedPic());
    }

    @Test
    void shouldSetStarterPicsWhenSigningUp() {
        ProfilePicture starterPicture = new ProfilePicture();
        List<ProfilePicture> starterPictures = List.of(starterPicture);
        SignUpRequestDTO requestDTO = new SignUpRequestDTO("User", "User", "password123",
                "password123", "user@example.com");

        when(pictureRepository.findAllByCategoryName("STARTER")).thenReturn(starterPictures);

        User updatedUser = authenticationHelper.buildUser(requestDTO);

        assertEquals(starterPictures, updatedUser.getProfilePictures());

    }

    @Test
    void shouldSetEmptyRoleNameIfNotPresent() {
        Role userRole = new Role();
        Set<Role> roles = Set.of(userRole);
        SignUpRequestDTO requestDTO = new SignUpRequestDTO("User", "User", "password123",
                "password123", "user@example.com");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        User updatedUser = authenticationHelper.buildUser(requestDTO);

        assertEquals(roles, updatedUser.getRoles());

    }

    @Test
    void shouldReturnBuiltUser() {
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        Set<Role> roles = Set.of(userRole);
        SignUpRequestDTO requestDTO = new SignUpRequestDTO("User", "User", "password123",
                "password123", "user@example.com");
        String encodedPassword = "encoded";

        when(passwordEncoder.encode(requestDTO.password())).thenReturn(encodedPassword);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        User builtUser = authenticationHelper.buildUser(requestDTO);

        assertEquals(requestDTO.email(), builtUser.getEmail());
        assertEquals(requestDTO.firstName(), builtUser.getFirstName());
        assertEquals(requestDTO.lastName(), builtUser.getLastName());
        assertEquals(roles, builtUser.getRoles());
    }
}
