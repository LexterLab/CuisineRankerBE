package com.cranker.cranker.bootstrap;

import com.cranker.cranker.ingredient.IngredientRepository;
import com.cranker.cranker.profile_pic.repository.ProfilePictureCategoryRepository;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
import com.cranker.cranker.recipe.RecipeRepository;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("docker")
public class DataCommandLineRunner implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final ProfilePictureCategoryRepository profilePictureCategoryRepository;
    private final ProfilePictureRepository profilePictureRepository;

    @Override
    public void run(String... args) {
        if(roleRepository.count() == 0 && userRepository.count() == 0
                && recipeRepository.count() == 0 && ingredientRepository.count() == 0
                && profilePictureCategoryRepository.count() == 0 && profilePictureRepository.count() == 0) {
            BootstrapHelper.setUp(roleRepository, userRepository, recipeRepository, ingredientRepository,
                    profilePictureCategoryRepository, profilePictureRepository);
        }
    }
}
