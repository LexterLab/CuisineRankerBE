package com.cranker.cranker.recipe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/recipes")
@Tag(name = "Recipe REST API Resource")
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("personal")
    @Operation(
            summary = "Retrieve User's Personal recipes REST API",
            description = "Retrieve User's Personal recipes REST API is used to retrieve every recipe a user has made"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "400", description = "Http Status 400 BAD REQUEST"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RecipeDTO>> retrieveUserPersonalRecipes(Authentication authentication) {
        return ResponseEntity.ok(recipeService.getAllRecipesByUser(authentication.getName()));
    }

    @Operation(
            summary = "Delete User's Personal recipe REST API",
            description = "Delete User's Personal recipe REST API is used to delete a single personal recipe"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "204", description = "Http Status 200 NO CONTENT"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("personal/{recipeId}")
    public ResponseEntity<Void> deleteUserPersonalRecipe(@PathVariable @Schema(example = "1") Long recipeId,
                                                         Authentication authentication) {
        recipeService.deletePersonalRecipe(authentication.getName(), recipeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Create personal recipe REST API",
            description = "Create personal recipe REST API is used to create a user's personal recipe"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "201", description = "Http Status 200 CREATED"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED"),
            @ApiResponse( responseCode = "403", description = "Http Status 403 FORBIDDEN"),
            @ApiResponse( responseCode = "404", description = "Http Status 404 NOT FOUND"),
            @ApiResponse( responseCode = "409", description = "Http Status 409 CONFLICT")
    })
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @PreAuthorize("hasRole('USER')")
    @PostMapping("personal")
    public ResponseEntity<RecipeDTO> createPersonalRecipe(Authentication authentication, @Valid @RequestBody RecipeRequestDTO requestDTO) {
        return new ResponseEntity<>(recipeService
                .createPersonalRecipe(authentication.getName(), requestDTO), HttpStatus.CREATED);
    }
}
