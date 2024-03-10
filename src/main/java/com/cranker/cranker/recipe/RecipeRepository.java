package com.cranker.cranker.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByUserEmail(String email);
    Optional<Recipe> findByIdAndUserId(Long recipeId, Long userId);
}
