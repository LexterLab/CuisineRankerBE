package com.cranker.cranker.recipe;

import app.tozzi.repository.JPASearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JPASearchRepository<Recipe> {
}
