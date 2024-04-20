package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.friendship.Friendship;
import com.cranker.cranker.friendship.FriendshipDTO;
import com.cranker.cranker.friendship.FriendshipRepository;
import com.cranker.cranker.friendship.FriendshipResponse;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.model.ProfilePictureCategory;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import com.cranker.cranker.user.UserService;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfilePictureRepository pictureRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private UserService service;




    @Test
    public void shouldReturnUserInfoWhenGivenValidEmail() {
        String email = "michael@example.com";
        User expectedUserInfo = new User();
        expectedUserInfo.setEmail(email);

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("url");

        expectedUserInfo.setSelectedPic(profilePicture);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(expectedUserInfo));

        UserDTO result = service.retrieveUserInfo(email);

        assertEquals(email, result.email());
    }

    @Test
    public void shouldThrowResourceNotFoundWhenGivenInvalidEmail() {
        String invalidEmail = "george@example.com";

        UserRequestDTO requestDTO = new UserRequestDTO("Alfred", "Hitch");

        when(userRepository.findUserByEmailIgnoreCase(invalidEmail)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.retrieveUserInfo(invalidEmail));
        assertThrows(ResourceNotFoundException.class, () -> service.changeUserPersonalInfo(invalidEmail, requestDTO));
    }

     @Test
     void shouldChangeUserPersonalInfoWhenProvidedValidEmail() {
        UserRequestDTO expectedUserInfo = new UserRequestDTO("Alfred", "Hitch");
        String email = "michael@example.com";
        User user = new User();


        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserRequestDTO changedUserInfo = service.changeUserPersonalInfo(email, expectedUserInfo);

        assertEquals(expectedUserInfo, changedUserInfo);
    }

    @Test
    void shouldRetrieveUserProfilePictures() {
        String email = "michael@example.com";
        ProfilePictureCategory category = new ProfilePictureCategory(1L, "STARTER");
        ProfilePicture picture = new ProfilePicture();
        picture.setId(1L);
        picture.setName("Rattingam");
        picture.setUrl("url");
        picture.setCategory(category);
        PictureDTO pictureDTO = new PictureDTO(picture.getId(), picture.getName(), picture.getUrl(),
                picture.getCategory().getName());
        List<PictureDTO> userPictures = List.of(pictureDTO);
        User user = new User();
        user.setProfilePictures(List.of(picture));


        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));

        List<PictureDTO> retrievedPictures = service.retrieveUserProfilePictures(email);

        assertEquals(userPictures, retrievedPictures);
    }

    @Test
    void shouldThrowResourceNotFoundWhenProvidedUnExistingEmailWhenRetrievingUserPictures() {
        String unexisting = "michael2@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.retrieveUserProfilePictures(unexisting));
    }

    @Test
    void shouldChangeUserProfilePicture() {
        String email = "michael@example.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setIsVerified(true);
        user.setIsTwoFactorEnabled(true);

        Long pictureId = 1L;
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setId(pictureId);
        profilePicture.setUrl("url");

        user.setSelectedPic(profilePicture);
        UserDTO expectedUser = new UserDTO(user.getId(), null, user.getEmail(),
                user.getSelectedPic().getUrl(), user.getIsVerified(), user.getIsTwoFactorEnabled());

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(pictureRepository.findById(pictureId)).thenReturn(Optional.of(profilePicture));
        when(userRepository.save(user)).thenReturn(user);

        UserDTO updatedUser = service.changeUserProfilePicture(email, pictureId);

        assertEquals(expectedUser.profilePicURL(), updatedUser.profilePicURL());
    }

    @Test
    void shouldThrowResourceNotFoundWhenProvidedUnExistingEmailWhenChangingProfilePicture() {
        String unexisting = "michael323@example.com";


        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.changeUserProfilePicture(unexisting, 1L));
    }

    @Test
    void shouldThrowResourceNotFoundWhenProvidedUnExistingPictureIdentifierWhenChangingProfilePicture() {
        String email = "michael@example.com";
        long unexistingId = 0L;

        assertThrows(ResourceNotFoundException.class, () -> service.changeUserProfilePicture(email, unexistingId));
    }

    @Test
    void shouldRetrieveUserFriendList() {
        String email = "michael@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        User friend = new User();
        friend.setId(2L);
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("ur;");
        friend.setSelectedPic(profilePicture);
        friend.setFirstName("Friend");
        friend.setLastName("Friend");

        String sortBy = "updatedAt";
        int pageNo = 0;
        int pageSize = 10;

        Friendship friendship = new Friendship(1L, "Active", user, friend, LocalDateTime.now(), LocalDateTime.now());
        String updatedAtFormatted = friendship.getUpdatedAt().getDayOfMonth() + " " + DateTimeFormatter
                .ofPattern("MMMM").format(friendship.getUpdatedAt()) + " " + friendship.getUpdatedAt().getYear();

        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, 2L, "Friend Friend", profilePicture.getUrl(),
                friendship.getStatus(), updatedAtFormatted, friendship.getCreatedAt(), friendship.getUpdatedAt());


        FriendshipResponse friendshipResponse = new FriendshipResponse(0, 10 , 1L,
                1, true, List.of(friendshipDTO));

        Sort sort = Sort.by(sortBy).ascending();



        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Friendship> friendshipPage = new PageImpl<>(List.of(friendship), pageable, 1);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findAllFriendsByUserId(user.getId(), pageable)).thenReturn(friendshipPage);

        FriendshipResponse response = service.retrieveUserFriends(email,0, 10, "updatedAt", "asc");

        assertEquals(friendshipResponse, response);
    }

    @Test
    void shouldThrowResourceNotFoundWhenUserNotFoundRequestingUserFriendList() {
        String email = "unexisting@example.com";

        when(userRepository.findUserByEmailIgnoreCase(email)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service
                .retrieveUserFriends(email,0, 10, "updatedAt", "asc"));
    }


}
