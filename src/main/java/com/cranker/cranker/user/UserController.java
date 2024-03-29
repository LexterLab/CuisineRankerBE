package com.cranker.cranker.user;

import com.cranker.cranker.user.payload.UserDTO;
import com.cranker.cranker.user.payload.UserRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

}
