package com.cranker.cranker.cloud;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cloud REST API's for Cloud environment")
@RequestMapping("api/v1/cloud/files")
public class CloudFileController {

    private final CloudFileService cloudFileService;


    @Operation(
            summary = "Upload file REST API",
            description = "Upload file REST API is used to upload files to google cloud storage"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 204 CREATED"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CloudFileDTO> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        return new ResponseEntity<>(cloudFileService.uploadFileToCloud(file, authentication.getName()), HttpStatus.CREATED);
    }
}
