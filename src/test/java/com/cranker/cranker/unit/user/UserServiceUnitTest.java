package com.cranker.cranker.unit.user;

import com.cranker.cranker.exception.APIException;
import com.cranker.cranker.exception.ResourceNotFoundException;
import com.cranker.cranker.friendship.*;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.model.ProfilePictureCategory;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
import com.cranker.cranker.token.TokenService;
import com.cranker.cranker.token.payload.TokenDTO;
import com.cranker.cranker.user.model.User;
import com.cranker.cranker.user.repository.UserRepository;
import com.cranker.cranker.user.UserService;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import com.cranker.cranker.user.payload.UserResponse;
import com.cranker.cranker.utils.PageableUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfilePictureRepository pictureRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private FriendshipHelper friendshipHelper;

    @Mock
    private PageableUtil pageableUtil;

    @Mock
    private TokenService tokenService;

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
        user.setSocialUsers(new ArrayList<>());

        Long pictureId = 1L;
        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setId(pictureId);
        profilePicture.setUrl("url");

        user.setSelectedPic(profilePicture);
        UserDTO expectedUser = new UserDTO(user.getId(), null, user.getEmail(),
                user.getSelectedPic().getUrl(), user.getIsVerified(), user.getIsTwoFactorEnabled(), false);

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
        when(pageableUtil.getPageable(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(pageable);

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

    @Test
    void shouldSendFriendshipRequest() {
        String email = "michael323@example.com";

        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("url;");

        User friend = new User();
        friend.setId(2L);
        friend.setFirstName("First");
        friend.setLastName("Last");
        friend.setSelectedPic(profilePicture);


        Friendship friendship = new Friendship();
        friendship.setId(1L);
        friendship.setFriend(friend);
        friendship.setUser(user);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);
        LocalDateTime date = LocalDateTime
                .of(2024, 5, 13, 0, 0, 0, 0);
        friendship.setUpdatedAt(date);
        friendship.setCreatedAt(date);
        String friendName = friendship.getFriend().getFirstName() + " " + friendship.getFriend().getLastName();


        FriendshipDTO expectedFriendship = new FriendshipDTO(friendship.getId(), friend.getId(), friendName,
                friend.getSelectedPic().getUrl(),FriendshipStatus.PENDING.getName(),
                "13 May 2024", date, date);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(friend.getId())).thenReturn(Optional.of(friend));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);
        doNothing().when(friendshipHelper).validateFriendshipRequest(user.getId(), friend.getId(), email);

        FriendshipDTO pendingFriendship = service.sendFriendRequest(email, friend.getId());

        assertEquals(expectedFriendship, pendingFriendship);
    }


    @Test
    void shouldThrowResourceNotFoundExceptionWhenSendingFriendshipRequestWithUnexistingUser() {
        String unExistingEmail = "michael323@example.com";


        when(userRepository.findUserByEmailIgnoreCase(unExistingEmail)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.sendFriendRequest(unExistingEmail,1L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenSendingFriendshipRequestWithUnexistingFriend() {
        long unexistingFriendId = 2L;

        User user = new User();
        user.setEmail("michael323@example.com");

        when(userRepository.findUserByEmailIgnoreCase("michael323@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(unexistingFriendId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.sendFriendRequest("michael323@example.com",
                unexistingFriendId));
    }

    @Test
    void shouldAcceptFriendshipRequest() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("url;");
        user.setSelectedPic(profilePicture);

        long friendshipId = 1L;
        Friendship friendship = new Friendship();
        friendship.setId(friendshipId);
        friendship.setUser(user);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);
        LocalDateTime date = LocalDateTime
                .of(2024, 5, 13, 0, 0, 0, 0);
        friendship.setUpdatedAt(date);
        friendship.setCreatedAt(date);
        String friendName = friendship.getUser().getFirstName() + " " + friendship.getUser().getLastName();

        FriendshipDTO expectedFriendship = new FriendshipDTO(friendship.getId(), user.getId(), friendName,
                user.getSelectedPic().getUrl(),FriendshipStatus.ACTIVE.getName(),
                "13 May 2024", date, date);

        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        doNothing().when(friendshipHelper).validatePendingFriendshipRequest(user, friendship, friendshipId);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendshipDTO acceptedFriendship = service.acceptFriendRequest(email, friendshipId);

        assertEquals(expectedFriendship, acceptedFriendship);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserAcceptedFriendshipRequest() {
        String unexisting = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.acceptFriendRequest(unexisting, 1L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingAcceptingUnExistingFriendshipRequest() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        long unexistingFriendshipId = 2L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(unexistingFriendshipId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.acceptFriendRequest(email, unexistingFriendshipId));
    }

    @Test
    void shouldRetrieveUserFriendshipRequests() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);


        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("ur;");
        user.setSelectedPic(profilePicture);
        user.setFirstName("Friend");
        user.setLastName("Friend");

        User friend = new User();
        friend.setId(2L);

        String sortBy = "updatedAt";
        int pageNo = 0;
        int pageSize = 10;

        Friendship friendship = new Friendship(1L, "Pending", user, friend, LocalDateTime.now(),
                LocalDateTime.now());
        String updatedAtFormatted = friendship.getUpdatedAt().getDayOfMonth() + " " + DateTimeFormatter
                .ofPattern("MMMM").format(friendship.getUpdatedAt()) + " " + friendship.getUpdatedAt().getYear();


        FriendshipDTO friendshipDTO = new FriendshipDTO(1L, 1L, "Friend Friend", profilePicture.getUrl(),
                friendship.getStatus(), updatedAtFormatted, friendship.getCreatedAt(), friendship.getUpdatedAt());

        FriendshipResponse friendshipResponse = new FriendshipResponse(0, 10 , 1L,
                1, true, List.of(friendshipDTO));

        Sort sort = Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Friendship> friendshipPage = new PageImpl<>(List.of(friendship), pageable, 1);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findAllFriendRequests(user.getId(), pageable)).thenReturn(friendshipPage);
        when(pageableUtil.getPageable(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(pageable);

        FriendshipResponse friendshipRequests = service.retrieveUserFriendshipRequests(email, 0, 10,
                "updatedAt", "asc");

        assertEquals(friendshipResponse, friendshipRequests);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserRetrievingFriendshipRequests() {
        String unexisting = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service
                .retrieveUserFriendshipRequests(unexisting, 0, 10, "updatedAt", "asc"));
    }

    @Test
    void shouldRetrieveUserSentFriendshipRequests() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);


        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("ur;");
        User friend = new User();
        friend.setId(2L);
        friend.setSelectedPic(profilePicture);
        friend.setFirstName("Friend");
        friend.setLastName("Friend");

        String sortBy = "updatedAt";
        int pageNo = 0;
        int pageSize = 10;

        Friendship friendship = new Friendship(1L, "Pending", user, friend, LocalDateTime.now(),
                LocalDateTime.now());
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
        when(friendshipRepository.findAllSentFriendRequests(user.getId(), pageable)).thenReturn(friendshipPage);
        when(pageableUtil.getPageable(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(pageable);

        FriendshipResponse friendshipRequests = service.retrieveUserSentFriendshipRequests(email, 0, 10,
                "updatedAt", "asc");

        assertEquals(friendshipResponse, friendshipRequests);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserRetrievingSentFriendshipRequests() {
        String unexisting = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.retrieveUserSentFriendshipRequests(unexisting, 0, 10,
                "updatedAt", "asc"));
    }

    @Test
    void shouldRejectFriendshipRequest() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        long friendshipId = 1L;
        Friendship friendship = new Friendship();
        friendship.setId(friendshipId);
        friendship.setUser(user);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);
        LocalDateTime date = LocalDateTime
                .of(2024, 5, 13, 0, 0, 0, 0);
        friendship.setUpdatedAt(date);
        friendship.setCreatedAt(date);

        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        doNothing().when(friendshipHelper).validatePendingFriendshipRequest(user, friendship, friendshipId);
        doNothing().when(friendshipRepository).delete(any(Friendship.class));

        service.rejectFriendRequest(email, friendshipId);

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserRejectsFriendshipRequest() {
        String unexisting = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.rejectFriendRequest(unexisting, 1L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenRejectingUnexistingFriendshipRequest() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        long unexistingFriendshipId = 1L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(unexistingFriendshipId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.rejectFriendRequest(email, unexistingFriendshipId));
    }

    @Test
    void shouldSearchAvailableUsersToAddAsAFriend() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("url");

        User friend = new User();
        friend.setId(2L);
        friend.setEmail("email");
        friend.setFirstName("Friend");
        friend.setLastName("Friend");
        friend.setSelectedPic(profilePicture);
        friend.setIsVerified(true);
        friend.setIsTwoFactorEnabled(false);

        String sortBy = "updatedAt";
        int pageNo = 0;
        int pageSize = 10;

        UserDTO userDTO = new UserDTO(friend.getId(),
                "Friend Friend", friend.getEmail(), "url", true, false, false);

        Sort sort = Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<User> userPage = new PageImpl<>(List.of(friend), pageable, 1);

        UserResponse expectedResponse = new UserResponse(0, 10 , 1L,
                1, true, List.of(userDTO));

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(pageableUtil.getPageable(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(pageable);
        when(userRepository.findAllByNameAndNotFriends("name", user, pageable)).thenReturn(userPage);

        UserResponse response = service.searchUsers(email, "name", 0, 10,
                "id", "asc");

        assertEquals(expectedResponse, response);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserSearchesThroughUsers() {
        String unexisting = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.searchUsers(unexisting, "name", 0, 10,
                "id", "asc"));
    }

    @Test
    void shouldGenerateFriendshipToken() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);

        String token = "friendshipToken";
        TokenDTO tokenDTO = new TokenDTO(token);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(tokenService.generateToken(user)).thenReturn(token);

        TokenDTO friendshipToken = service.generateFriendshipToken(email);

        assertEquals(tokenDTO, friendshipToken);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserGeneratesFriendshipToken() {
        String unexisting = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.generateFriendshipToken(unexisting));
    }

    @Test
    void shouldAddFriendViaToken() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        String token = "friendshipToken";
        TokenDTO tokenDTO = new TokenDTO(token);

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("url");
        User friend = new User();
        friend.setId(2L);
        friend.setFirstName("First");
        friend.setLastName("Last");
        friend.setSelectedPic(profilePicture);

        Friendship friendship = new Friendship();
        friendship.setId(3L);
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);
        LocalDateTime date = LocalDateTime
                .of(2024, 5, 13, 0, 0, 0, 0);
        friendship.setUpdatedAt(date);
        friendship.setCreatedAt(date);
        String friendName = friendship.getFriend().getFirstName() + " " + friendship.getFriend().getLastName();

        FriendshipDTO expectedFriendship = new FriendshipDTO(friendship.getId(), friend.getId(), friendName,
                friend.getSelectedPic().getUrl(),FriendshipStatus.ACTIVE.getName(),
                "13 May 2024", date, date);


        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        doNothing().when(tokenService).confirmToken(any(String.class));
        when(tokenService.getUserByToken(tokenDTO.value())).thenReturn(friend);
        when(friendshipRepository.friendshipExists(user.getId(), friend.getId(), FriendshipStatus.PENDING.getName()))
                .thenReturn(false);
        doNothing().when(friendshipHelper)
                .validateFriendshipRequest(any(Long.class), any(Long.class), any(String.class));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendshipDTO friendshipViaToken = service.addFriendViaToken(email, tokenDTO);

        assertEquals(expectedFriendship, friendshipViaToken);
    }

    @Test
    void shouldChangeExistingPendingFriendshipToActiveWhenAddingViaToken() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        String token = "friendshipToken";
        TokenDTO tokenDTO = new TokenDTO(token);

        ProfilePicture profilePicture = new ProfilePicture();
        profilePicture.setUrl("url");
        User friend = new User();
        friend.setId(2L);
        friend.setFirstName("First");
        friend.setLastName("Last");
        friend.setSelectedPic(profilePicture);

        Friendship friendship = new Friendship();
        friendship.setId(3L);
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);
        LocalDateTime date = LocalDateTime
                .of(2024, 5, 13, 0, 0, 0, 0);
        friendship.setUpdatedAt(date);
        friendship.setCreatedAt(date);
        String friendName = friendship.getFriend().getFirstName() + " " + friendship.getFriend().getLastName();

        FriendshipDTO expectedFriendship = new FriendshipDTO(friendship.getId(), friend.getId(), friendName,
                friend.getSelectedPic().getUrl(),FriendshipStatus.ACTIVE.getName(),
                "13 May 2024", date, date);


        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        doNothing().when(tokenService).confirmToken(any(String.class));
        when(tokenService.getUserByToken(tokenDTO.value())).thenReturn(friend);
        when(friendshipRepository.friendshipExists(user.getId(), friend.getId(), FriendshipStatus.PENDING.getName()))
                .thenReturn(true);
        when(friendshipRepository.findByUserIdAndFriendId(user.getId(), friend.getId())).thenReturn(Optional.of(friendship));
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        FriendshipDTO friendshipViaToken = service.addFriendViaToken(email, tokenDTO);

        assertEquals(expectedFriendship, friendshipViaToken);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserAddsFriendViaToken() {
        String unexisting = "unexisting@gmail.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service
                .addFriendViaToken(unexisting, new TokenDTO("token")));
    }

    @Test
    void shouldThrowAPIExceptionWhenAddingYourselfViaFriendToken() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);
        String token = "friendshipToken";
        TokenDTO tokenDTO = new TokenDTO(token);

        User friend = new User();
        friend.setId(1L);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        doNothing().when(tokenService).confirmToken(any(String.class));
        when(tokenService.getUserByToken(tokenDTO.value())).thenReturn(friend);

        assertThrows(APIException.class, () -> service.addFriendViaToken(email, tokenDTO));
    }

    @Test
    void shouldCancelFriendshipRequest() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        long friendshipId = 1L;
        Friendship friendship = new Friendship();
        friendship.setId(friendshipId);
        friendship.setUser(user);
        friendship.setFriendshipStatus(FriendshipStatus.PENDING);

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
        doNothing().when(friendshipRepository).delete(any(Friendship.class));

        service.cancelFriendshipRequest(email, friendshipId);

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUnexistingUserCancelsFriendshipRequest() {
        String unexistingEmail = "michael323@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexistingEmail)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.cancelFriendshipRequest(unexistingEmail, 1L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenCancellingUnexistingFriendshipRequest() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        long unexistingFriendshipId = 1L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(unexistingFriendshipId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.cancelFriendshipRequest(email, unexistingFriendshipId));
    }

    @Test
    void shouldRemoveFriendship() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(1L);

        User friend = new User();
        friend.setId(2L);

        Friendship friendship = new Friendship();
        friendship.setId(1L);
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setFriendshipStatus(FriendshipStatus.ACTIVE);

        long friendshipId = 1L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(friendshipId)).thenReturn(Optional.of(friendship));
        doNothing().when(friendshipRepository).delete(friendship);

        service.removeFriendship(email, friendshipId);

        verify(friendshipRepository).delete(friendship);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistWhenRemovingFriendship() {
        String unexisting = "unexisting@example.com";

        when(userRepository.findUserByEmailIgnoreCase(unexisting)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> service.removeFriendship(unexisting, 1L));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenFriendshipDoesNotExistWhenRemovingFriendship() {
        String email = "michael323@example.com";
        User user = new User();
        user.setEmail(email);

        long unexistingFriendshipId = 1L;

        when(userRepository.findUserByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(unexistingFriendshipId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.removeFriendship(email, unexistingFriendshipId));
    }
}
