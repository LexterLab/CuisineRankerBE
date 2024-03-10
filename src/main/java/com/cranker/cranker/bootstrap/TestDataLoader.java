package com.cranker.cranker.bootstrap;

import com.cranker.cranker.ingredient.IngredientRepository;
import com.cranker.cranker.recipe.RecipeRepository;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("test")
public class TestDataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    @Override
    public void run(String... args) {
        if(roleRepository.count() == 0 && userRepository.count() == 0
                && recipeRepository.count() == 0 && ingredientRepository.count() == 0) {
            BootstrapHelper.setUp(roleRepository, userRepository, recipeRepository, ingredientRepository);
        }
    }
}
