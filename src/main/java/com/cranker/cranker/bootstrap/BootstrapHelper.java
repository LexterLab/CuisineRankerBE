package com.cranker.cranker.bootstrap;

import com.cranker.cranker.ingredient.AmountType;
import com.cranker.cranker.ingredient.Ingredient;
import com.cranker.cranker.ingredient.IngredientRepository;
import com.cranker.cranker.recipe.Recipe;
import com.cranker.cranker.recipe.RecipeIngredient;
import com.cranker.cranker.recipe.RecipeRepository;
import com.cranker.cranker.recipe.RecipeType;
import com.cranker.cranker.role.Role;
import com.cranker.cranker.role.RoleRepository;
import com.cranker.cranker.user.User;
import com.cranker.cranker.user.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class BootstrapHelper {
    static void setUp(RoleRepository roleRepository, UserRepository userRepository, RecipeRepository recipeRepository,
                      IngredientRepository ingredientRepository) {
        Role adminRole = new Role();
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        adminRole.setName("ROLE_ADMIN");
        roleRepository.saveAll(List.of(userRole, adminRole));

        User admin = new User();
        admin.setFirstName("admin");
        admin.setLastName("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(new BCryptPasswordEncoder().encode("!Admin123"));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setIsVerified(true);
        admin.setRoles(Set.of(adminRole, userRole));

        User user = new User();
        user.setFirstName("user");
        user.setLastName("user");
        user.setEmail("user@gmail.com");
        user.setPassword(new BCryptPasswordEncoder().encode("!user123"));
        user.setCreatedAt(LocalDateTime.now());
        user.setIsVerified(true);
        user.setIsTwoFactorEnabled(false);
        user.setRoles(Set.of(userRole));


        userRepository.saveAll(List.of(admin, user));

        userRepository.confirmEmail(user.getEmail());

        Ingredient ingredient = new Ingredient();
        ingredient.setAmountType(AmountType.MG);
        ingredient.setName("Chicken Breasts Oiled");
        ingredient.setDefaultAmount(100.00);
        ingredientRepository.save(ingredient);

        Recipe chickenBreasts = getRecipe(user);

        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setRecipe(chickenBreasts);
        recipeIngredient.setIngredientAmount(200.00);

        chickenBreasts.getRecipeIngredients().add(recipeIngredient);

        recipeRepository.save(chickenBreasts);
    }

    private static Recipe getRecipe(User user) {
        Recipe chickenBreasts = new Recipe();
        chickenBreasts.setPreparation("Just Do it");
        chickenBreasts.setUser(user);
        chickenBreasts.setName("Chicken Breasts");
        chickenBreasts.setType(RecipeType.CUSTOM);
        chickenBreasts.setCookTimeInMinutes(1);
        chickenBreasts.setPrepTimeInMinutes(1);
        chickenBreasts.setTotalTimeInMinutes(2);
        chickenBreasts.setPictureURL("https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcT9XfLsoBK13bXlmWzSKj1PSts6ocsDZRsZHpNb4zTGar4WQ5ezGSlI-OdOND9W2BFD");
        return chickenBreasts;
    }
}
