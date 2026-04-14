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
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository, SpoonacularClient spoonacularClient, RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.spoonacularClient = spoonacularClient;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
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
            Recipe recipe = getOrSaveRecipe(recipeDTO);
            recipes.add(recipe);
        }

        MealPlan mealPlan = new MealPlan();
        mealPlan.setDaysCount(request.getDaysCount());
        mealPlan.setStartDate(LocalDate.now());
        //mealPlan.setRecipes(recipes);
        int index = 0;

        for (int i = 0; i <recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            int day = (index % request.getDaysCount()) + 1;
            String dishType = dishTypes.get(i / request.getDaysCount());
            mealPlan.addRecipe(recipe, day, dishType);
            index++;
        }
//        for (Recipe recipe : recipes) {
//            int day = (index % request.getDaysCount()) + 1;
//            mealPlan.addRecipe(recipe, day);
//            index++;
//        }
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

    private Recipe getOrSaveRecipe(SpoonacularRecipeDTO spoonacularRecipeDTO) {
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
                                        ? ingredientDTO.getUnit() : "-"
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

    public List<ShoppingListItemDTO> getShoppingList(Long mealPlanId) {
        MealPlan mealPlan = getMealPlanById(mealPlanId);

        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        for (MealPlanRecipe mealPlanRecipe : mealPlan.getMealPlanRecipes()) {
            Recipe recipe = mealPlanRecipe.getRecipe();
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                String ingredientName = recipeIngredient.getIngredient().getName();
                String unit = recipeIngredient.getUnit();
                BigDecimal amount = recipeIngredient.getAmount();

                String key = ingredientName + ", " + unit;
                totals.put(key, totals.getOrDefault(key, BigDecimal.ZERO).add(amount));
            }
        }

        List<ShoppingListItemDTO> shoppingList = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            String[] parts = entry.getKey().split(", ");
            String ingredientName = parts[0];
            String unit = parts[1];
            BigDecimal amount = entry.getValue();

            shoppingList.add(new ShoppingListItemDTO(ingredientName, amount, unit));
        }
        return shoppingList;
    }

//    @Transactional
//    public MealPlan replaceRecipe(Long mealPlanId, Long recipeId) {
//        MealPlan mealPlan = getMealPlanById(mealPlanId);
//
//        int index = -1;
//        for (int i = 0; i < mealPlan.getRecipes().size(); i++) {
//            if (mealPlan.getRecipes().get(i).getId().equals(recipeId)) {
//                index = i;
//                break;
//            }
//        }
//
//        if (index == -1) {
//            throw new RuntimeException("Recipe not found in meal plan");
//        }
//
//        SpoonacularRecipeDTO newRecipeDTO;
//
//        if (mealPlan.getDiet() != null && !mealPlan.getDiet().isEmpty()) {
//            List<SpoonacularRecipeDTO> newRecipes = spoonacularClient.getRandomRecipesByDiets(mealPlan.getDiet(), 1);
//            newRecipeDTO = newRecipes.get(0);
//        } else {
//            List<SpoonacularRecipeDTO> newRecipes = spoonacularClient.getRandomRecipes(1);
//            newRecipeDTO = newRecipes.get(0);
//        }
//
//        Recipe newRecipe = getOrSaveRecipe(newRecipeDTO);
//
//        mealPlan.getRecipes().set(index, newRecipe);
//
//        return mealPlanRepository.save(mealPlan);
//    }

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

        Recipe newRecipe = getOrSaveRecipe(newRecipeDTO);

        target.setRecipe(newRecipe);

        return mealPlanRepository.save(mealPlan);
    }

}
