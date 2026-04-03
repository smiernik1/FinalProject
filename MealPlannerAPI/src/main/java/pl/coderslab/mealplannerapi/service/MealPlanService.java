package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.dto.CreateMealPlanRequestDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularIngredientDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.entity.Ingredient;
import pl.coderslab.mealplannerapi.entity.MealPlan;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.entity.RecipeIngredient;
import pl.coderslab.mealplannerapi.repository.IngredientRepository;
import pl.coderslab.mealplannerapi.repository.MealPlanRepository;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final SpoonacularClient spoonacularClient;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    public MealPlanService(MealPlanRepository mealPlanRepository, SpoonacularClient spoonacularClient, RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.spoonacularClient = spoonacularClient;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public MealPlan generateMealPlan(CreateMealPlanRequestDTO request) {
        List<SpoonacularRecipeDTO> spoonacularRecipeDTO = spoonacularClient.getRandomRecipes(request.getDaysCount());

        List<Recipe>  recipes = new ArrayList<>();

        for (SpoonacularRecipeDTO recipeDTO : spoonacularRecipeDTO) {
            Recipe recipe = getOrSaveRecipe(recipeDTO);
            recipes.add(recipe);
        }

        MealPlan mealPlan = new MealPlan();
        mealPlan.setDaysCount(request.getDaysCount());
        mealPlan.setStartDate(LocalDate.now());
        mealPlan.setRecipes(recipes);

        return mealPlanRepository.save(mealPlan);
    }

    private Recipe getOrSaveRecipe(SpoonacularRecipeDTO spoonacularRecipeDTO) {
        return recipeRepository.findByExternalId(spoonacularRecipeDTO.getId()).
                orElseGet(() -> saveNewRecipe(spoonacularRecipeDTO));
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
                                        ? ingredientDTO.getUnit() : "-"
                        )
                        .build();

                recipe.addRecipeIngredient(recipeIngredient);
            }
        }

        return recipeRepository.save(recipe);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public MealPlan getMealPlanById(Long id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't find mealPlan with id: " + id));

    }
}
