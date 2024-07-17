package com.cranker.cranker.notification.controller;

import com.cranker.cranker.notification.payload.NotificationResponseDTO;
import com.cranker.cranker.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(
            summary = "Get All user notifications REST API",
            description = "Get All user notifications REST API is used to retrieve user's notifications"
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
    @GetMapping("")
    public ResponseEntity<NotificationResponseDTO> getAllNotifications(
            Authentication auth,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "issued", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        NotificationResponseDTO response = notificationService
                .retrieveNotifications(auth.getName(), pageNo, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(response ,HttpStatus.OK);
    }

    @Operation(
            summary = "Dismiss notification REST API",
            description = "Dismiss notification REST API is used to delete notifications by Id"
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
    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> dismissNotification(Authentication authentication, @PathVariable Long notificationId) {
        notificationService.dismissNotification(authentication.getName(), notificationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
