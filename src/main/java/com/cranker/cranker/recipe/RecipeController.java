package com.cranker.cranker.recipe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
