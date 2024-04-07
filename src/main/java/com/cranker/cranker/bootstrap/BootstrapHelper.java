package com.cranker.cranker.bootstrap;

import com.cranker.cranker.ingredient.AmountType;
import com.cranker.cranker.ingredient.Ingredient;
import com.cranker.cranker.ingredient.IngredientRepository;
import com.cranker.cranker.profile_pic.model.ProfilePicture;
import com.cranker.cranker.profile_pic.model.ProfilePictureCategory;
import com.cranker.cranker.profile_pic.repository.ProfilePictureCategoryRepository;
import com.cranker.cranker.profile_pic.repository.ProfilePictureRepository;
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
                      IngredientRepository ingredientRepository, ProfilePictureCategoryRepository pictureCategoryRepository,
                      ProfilePictureRepository profilePictureRepository) {
        Role adminRole = new Role();
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        adminRole.setName("ROLE_ADMIN");
        roleRepository.saveAll(List.of(userRole, adminRole));

        ProfilePictureCategory starter = new ProfilePictureCategory();
        starter.setName("STARTER");
        pictureCategoryRepository.save(starter);

        ProfilePicture pfp1 = new ProfilePicture();
        pfp1.setCategory(starter);
        pfp1.setUrl("https://i.ibb.co/mbzZL5x/rat1.jpg");
        pfp1.setName("Rattingam");

        ProfilePicture pfp2 = new ProfilePicture();
        pfp2.setCategory(starter);
        pfp2.setName("Billy");
        pfp2.setUrl("https://i.ibb.co/HqxfyS4/male1.png");

        ProfilePicture pfp3 = new ProfilePicture();
        pfp3.setCategory(starter);
        pfp3.setName("Jasmine");
        pfp3.setUrl("https://i.ibb.co/MVZcQgh/female4.png");

        ProfilePicture pfp4 = new ProfilePicture();
        pfp4.setCategory(starter);
        pfp4.setName("William");
        pfp4.setUrl("https://i.ibb.co/Hd3bL9r/cat3.png");

        profilePictureRepository.saveAll(List.of(pfp1, pfp2, pfp3, pfp4));

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
        user.setSelectedPic(pfp4);
        user.setProfilePictures(List.of(pfp1, pfp2, pfp3, pfp4));


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
