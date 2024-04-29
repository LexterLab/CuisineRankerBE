package com.cranker.cranker.ingredient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/ingredients")
@Tag(name = "Ingredient REST APIs")
public class IngredientController {
    private final IngredientService ingredientService;


    @Operation(
            summary = "Search pageable ingredients by name REST API",
            description = "Search pageable ingredients by name REST API is used to retrieve pageable version of " +
                    "ingredients matching name"
    )
    @SecurityRequirement(
            name = "Bearer Authentication"
    )
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Http Status 200 SUCCESS"),
            @ApiResponse( responseCode = "401", description = "Http Status 401 UNAUTHORIZED")
    })
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<IngredientResponse> searchIngredients(
            @RequestParam String name,
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return ResponseEntity.ok(ingredientService.retrieveIngredientsSearched(name, pageNo, pageSize, sortBy, sortDir));
    }
}
