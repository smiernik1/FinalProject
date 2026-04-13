package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.dto.SpoonacularIngredientDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeResponseDTO;
import pl.coderslab.mealplannerapi.entity.Ingredient;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.entity.RecipeIngredient;
import pl.coderslab.mealplannerapi.repository.IngredientRepository;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

import java.math.BigDecimal;

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

    @Transactional
    public Recipe importRandomRecipe() {
        SpoonacularRecipeDTO spoonacularRecipeDTO = spoonacularClient.getRandomRecipeRaw();
        return recipeRepository.findByExternalId(spoonacularRecipeDTO.getId()).orElseGet(()->saveNewRecipe(spoonacularRecipeDTO));
    }

    private Recipe saveNewRecipe(SpoonacularRecipeDTO spoonacularRecipeDTO) {
        Recipe recipe =  Recipe.builder()
                .externalId(spoonacularRecipeDTO.getId())
                .name(spoonacularRecipeDTO.getTitle())
                .imageUrl(spoonacularRecipeDTO.getImage())
                .sourceUrl(spoonacularRecipeDTO.getSourceUrl())
                .calories(null)
                .build();
        if (spoonacularRecipeDTO.getExtendedIngredients() != null) {
            for (SpoonacularIngredientDTO ingredientDTO : spoonacularRecipeDTO.getExtendedIngredients() ) {

                Ingredient ingredient = ingredientRepository
                        .findByNameIgnoreCase(ingredientDTO.getName())
                        .orElseGet(() -> ingredientRepository.save(
                                Ingredient.builder()
                                        .name(ingredientDTO.getName())
                                        .build()
                        ));

                RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                        .ingredient(ingredient)
                        .amount(ingredientDTO.getAmount() != null ? ingredientDTO.getAmount() : BigDecimal.ZERO)
                        .unit(
                                ingredientDTO.getUnit() != null && !ingredientDTO.getUnit().isBlank()
                                ? ingredientDTO.getUnit() : "units"
                        )
                        .build();

                recipe.addRecipeIngredient(recipeIngredient);
            }
        }

        return recipeRepository.save(recipe);
    }
}
