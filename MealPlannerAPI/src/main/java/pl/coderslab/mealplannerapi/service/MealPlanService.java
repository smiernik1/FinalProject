package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.dto.CreateMealPlanRequestDTO;
import pl.coderslab.mealplannerapi.dto.ShoppingListItemDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularIngredientDTO;
import pl.coderslab.mealplannerapi.dto.SpoonacularRecipeDTO;
import pl.coderslab.mealplannerapi.entity.*;
import pl.coderslab.mealplannerapi.repository.IngredientRepository;
import pl.coderslab.mealplannerapi.repository.MealPlanRepository;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MealPlanService {
    private final MealPlanRepository mealPlanRepository;
    private final SpoonacularClient spoonacularClient;
    private final RecipeService recipeService;
    private final ShoppingListService shoppingListService;

    public MealPlanService(MealPlanRepository mealPlanRepository, SpoonacularClient spoonacularClient, RecipeRepository recipeRepository, IngredientRepository ingredientRepository, RecipeService recipeService, ShoppingListService shoppingListService, ShoppingListService shoppingListService1) {
        this.mealPlanRepository = mealPlanRepository;
        this.spoonacularClient = spoonacularClient;
        this.recipeService = recipeService;
        this.shoppingListService = shoppingListService1;
    }

    @Transactional
    public MealPlan generateMealPlan(CreateMealPlanRequestDTO request, List<String> dishTypes) {

        List<SpoonacularRecipeDTO> spoonacularRecipeDTO = new ArrayList<>();

        for (String dishType : dishTypes) {
            if (request.getDiet() != null && !request.getDiet().isEmpty()) {
                spoonacularRecipeDTO.addAll(spoonacularClient.getRandomRecipesByDiets(request.getDiet(), request.getDaysCount(), dishType));
            } else {
                spoonacularRecipeDTO.addAll(spoonacularClient.getRandomRecipes(request.getDaysCount(), dishType));
            }
        }

        List<Recipe> recipes = new ArrayList<>();

        for (SpoonacularRecipeDTO recipeDTO : spoonacularRecipeDTO) {
            Recipe recipe = recipeService.getOrSaveRecipe(recipeDTO);
            recipes.add(recipe);
        }

        MealPlan mealPlan = new MealPlan();
        mealPlan.setDaysCount(request.getDaysCount());
        mealPlan.setStartDate(LocalDate.now());

        int index = 0;

        for (int i = 0; i <recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            int day = (index % request.getDaysCount()) + 1;
            String dishType = dishTypes.get(i / request.getDaysCount());
            mealPlan.addRecipe(recipe, day, dishType);
            index++;
        }
        mealPlan.setDiet(request.getDiet());

        if (request.getMinCalories() != null &&
                request.getMaxCalories() != null &&
                request.getMaxCalories() <= request.getMinCalories()) {

            throw new IllegalArgumentException("Max calories must be > min calories");
        }
        mealPlan.setMinCalories(request.getMinCalories());
        mealPlan.setMaxCalories(request.getMaxCalories());

        return mealPlanRepository.save(mealPlan);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public MealPlan getMealPlanById(Long id) {
        return mealPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't find mealPlan with id: " + id));

    }

    public void deleteMealPlanById(Long id) {
        if (!mealPlanRepository.existsById(id)) {
            throw new RuntimeException("Can't find mealPlan with id: " + id);
        }
        mealPlanRepository.deleteById(id);
    }

    @Transactional
    public MealPlan replaceRecipe(Long mealPlanId, Long recipeId) {

        MealPlan mealPlan = getMealPlanById(mealPlanId);

        MealPlanRecipe target = null;

        for (MealPlanRecipe mpr : mealPlan.getMealPlanRecipes()) {
            if (mpr.getRecipe().getId().equals(recipeId)) {
                target = mpr;
                break;
            }
        }

        if (target == null) {
            throw new RuntimeException("Recipe not found in meal plan");
        }

        String dishType = target.getDishType();

        if (dishType == null || dishType.isBlank()) {
            throw new RuntimeException("Dish type missing for recipe in meal plan");
        }

        List<SpoonacularRecipeDTO> results;

        if (mealPlan.getDiet() != null && !mealPlan.getDiet().isBlank()) {
            results = spoonacularClient.getRandomRecipesByDiets(
                    mealPlan.getDiet(),
                    1,
                    dishType
            );
        } else {
            results = spoonacularClient.getRandomRecipes(
                    1,
                    dishType
            );
        }

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("No recipes found for replacement");
        }

        SpoonacularRecipeDTO newRecipeDTO = results.get(0);

        Recipe newRecipe = recipeService.getOrSaveRecipe(newRecipeDTO);

        target.setRecipe(newRecipe);

        return mealPlanRepository.save(mealPlan);
    }
}
