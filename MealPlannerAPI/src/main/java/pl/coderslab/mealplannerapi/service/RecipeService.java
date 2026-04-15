package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import pl.coderslab.mealplannerapi.dto.SpoonacularIngredientDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.entity.Ingredient;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.entity.RecipeIngredient;
import pl.coderslab.mealplannerapi.repository.IngredientRepository;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Recipe getRecipeById(Long id){
        return recipeRepository.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public void deleteRecipe(Long id){
        if(!recipeRepository.existsById(id)){
            throw new RuntimeException("Recipe not found");
        }
        recipeRepository.deleteById(id);
    }

    public Recipe getOrSaveRecipe(SpoonacularRecipeDTO spoonacularRecipeDTO) {
        return recipeRepository.findByExternalId(spoonacularRecipeDTO.getId()).
                orElseGet(() -> saveNewRecipe(spoonacularRecipeDTO));
    }

    private Recipe saveNewRecipe(SpoonacularRecipeDTO spoonacularRecipeDTO) {
        Recipe recipe = Recipe.builder()
                .externalId(spoonacularRecipeDTO.getId())
                .name(spoonacularRecipeDTO.getTitle())
                .imageUrl(spoonacularRecipeDTO.getImage())
                .sourceUrl(spoonacularRecipeDTO.getSourceUrl())
                .calories(extractCalories(spoonacularRecipeDTO))
                .build();
        if (spoonacularRecipeDTO.getExtendedIngredients() != null) {
            for (SpoonacularIngredientDTO ingredientDTO : spoonacularRecipeDTO.getExtendedIngredients()) {

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

    private Integer extractCalories(SpoonacularRecipeDTO dto) {
        if (dto.getNutrition() == null || dto.getNutrition().getNutrients() == null) {
            return null;
        }

        return dto.getNutrition().getNutrients().stream()
                .filter(n -> "Calories".equalsIgnoreCase(n.getName()))
                .findFirst()
                .map(n -> (int) Math.round(n.getAmount()))
                .orElse(null);
    }
}
