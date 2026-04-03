package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.repository.IngredientRepository;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

@Service
public class RecipeImportService {
    private final SpoonacularClient spoonacularClient;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    public RecipeImportService(SpoonacularClient spoonacularClient, RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.spoonacularClient = spoonacularClient;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

//    public Recipe importRandomRecipe() {
//        SpoonacularRecipeDTO spoonacularRecipeDTO = spoonacularClient.getRandomRecipeRaw();
//    }
}
