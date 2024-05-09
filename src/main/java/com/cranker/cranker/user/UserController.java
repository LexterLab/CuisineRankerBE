package com.cranker.cranker.user;

import com.cranker.cranker.friendship.FriendshipDTO;
import com.cranker.cranker.friendship.FriendshipResponse;
import com.cranker.cranker.profile_pic.payload.PictureDTO;
import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import com.cranker.cranker.user.payload.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
@Tag(name = "User REST API")
public class UserController {
    private final UserService service;

    @Operation(
            summary = "User info Retrieval REST API",
            description = "User info Retrieval REST API is used to retrieve user's info"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<UserDTO> getUserInfo(Authentication authentication) {
        return ResponseEntity.ok(service.retrieveUserInfo(authentication.getName()));
    }

    @Operation(
            summary = "User profile pictures Retrieval REST API",
            description = "User profile pictures Retrieval REST API is used to retrieve user's profile pictures"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/pictures")
    public ResponseEntity<List<PictureDTO>> getUserProfilePictures(Authentication authentication) {
        return ResponseEntity.ok(service.retrieveUserProfilePictures(authentication.getName()));
    }

    @Operation(
            summary = "Change User Personal Info REST API",
            description = "Change User Personal Info REST API is used to modify user's personal info"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("")
    public ResponseEntity<UserRequestDTO> changeUserPersonalInfo(Authentication authentication,
                                                                 @Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(service.changeUserPersonalInfo(authentication.getName(),requestDTO));
    }

    @Operation(
            summary = "Change User Profile Picture REST API",
            description = "Change User Profile Picture REST API is used to change user's profile picture"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/pictures/{pictureId}")
    public ResponseEntity<UserDTO> changeUserProfilePicture(Authentication authentication,
                                                                   @PathVariable Long pictureId) {
        return ResponseEntity.ok(service.changeUserProfilePicture(authentication.getName(), pictureId));
    }

    @Operation(
            summary = "Get authenticated user's friends REST API",
            description = "Get authenticated user's friends REST API is used to retrieve pageable version of user's friends"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("friends")
    public ResponseEntity<FriendshipResponse> retrieveUserFriends(
            Authentication authentication,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return ResponseEntity.ok(service.retrieveUserFriends(authentication.getName(), pageNo, pageSize, sortBy, sortDir));
    }


    @Operation(
            summary = "Request friendship to a user REST API",
            description = "Request friendship to a user REST API is used to create friendship request between two users"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 201 CREATED"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND"),
            @ApiResponse( responseCode = "409", description = "Http Status 409 CONFLICT")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping("friends/{friendId}")
    public ResponseEntity<FriendshipDTO> requestFriendship(Authentication authentication, @PathVariable Long friendId) {
        return new ResponseEntity<>(service.sendFriendRequest(authentication.getName(), friendId), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve user's friendship requests REST API",
            description = "Retrieve user's friendship requests REST API is used to retrieve signed user's friendship requests"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("friends/requests")
    public ResponseEntity<FriendshipResponse> retrieveUserFriendshipRequests(
            Authentication authentication,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return ResponseEntity.ok(service.retrieveUserFriendshipRequests(authentication.getName(), pageNo, pageSize, sortBy, sortDir));
    }

    @Operation(
            summary = "Retrieve user's sent friendship requests REST API",
            description = "Retrieve user's sent friendship requests REST API is used to retrieve signed in" +
                    " user's sent friendship requests"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("friends/requests/sent")
    public ResponseEntity<FriendshipResponse> retrieveUserSentFriendshipRequests(
            Authentication authentication,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return ResponseEntity.ok(service.retrieveUserSentFriendshipRequests(authentication.getName(), pageNo, pageSize,
                sortBy, sortDir));
    }

    @Operation(
            summary = "Accept user's friendship request REST API",
            description = "Accept user's friendship request REST API is used to update friendship to active"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND"),
            @ApiResponse( responseCode = "409", description = "Http Status 409 CONFLICT")
    })
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("friends/{friendshipId}")
    public ResponseEntity<FriendshipDTO> acceptFriendship(
            Authentication authentication,
            @Schema(description = "id of the friendship/ friend request", example = "1")
            @PathVariable Long friendshipId) {
        return ResponseEntity.ok(service.acceptFriendRequest(authentication.getName(), friendshipId));
    }

    @Operation(
            summary = "Reject friendship request REST API",
            description = "Reject friendship request REST API is used to delete friendship requests"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "204", description = "Http Status 200 NO CONTENT"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND"),
            @ApiResponse( responseCode = "409", description = "Http Status 409 CONFLICT"),
    })
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("friends/{friendshipId}/reject")
    public ResponseEntity<Void> rejectFriendship(
                    Authentication authentication,
                    @Schema(description = "id of the friendship/ friend request", example = "1")
                    @PathVariable Long friendshipId) {
        service.rejectFriendRequest(authentication.getName(), friendshipId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search users REST API",
            description = "Search users REST API is used to search users by name"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 OK"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping("search")
    public ResponseEntity<UserResponse> searchUsers(
            Authentication authentication,
            @Schema(example = "user")
            @RequestParam(required = false) String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {

        return ResponseEntity.ok(service.searchUsers(authentication.getName(), name, pageNo, pageSize, sortBy, sortDir));
    }
}
