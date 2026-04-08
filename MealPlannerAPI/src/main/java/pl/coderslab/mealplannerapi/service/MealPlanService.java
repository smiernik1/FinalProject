package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.mealplannerapi.SpoonacularClient;
import pl.coderslab.mealplannerapi.dto.CreateMealPlanRequestDTO;
import pl.coderslab.mealplannerapi.dto.ShoppingListItemDTO;
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
    public MealPlan generateMealPlan(CreateMealPlanRequestDTO request) {

        List<SpoonacularRecipeDTO> spoonacularRecipeDTO;

        // jeśli dieta jest ustawiona, filtruj przepisy po diecie
        if (request.getDiet() != null && !request.getDiet().isEmpty()) {
            spoonacularRecipeDTO = spoonacularClient.getRandomRecipesByDiets(request.getDiet(), request.getDaysCount());
            System.out.println("Dieta");
        } else {
            // inaczej pobierz losowe przepisy bez filtra
            spoonacularRecipeDTO = spoonacularClient.getRandomRecipes(request.getDaysCount());
            System.out.println("Bez diety");
        }

        List<Recipe> recipes = new ArrayList<>();

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
        Recipe recipe = Recipe.builder()
                .externalId(spoonacularRecipeDTO.getId())
                .name(spoonacularRecipeDTO.getTitle())
                .imageUrl(spoonacularRecipeDTO.getImage())
                .sourceUrl(spoonacularRecipeDTO.getSourceUrl())
                .calories(null)
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

        for (Recipe recipe : mealPlan.getRecipes()) {
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

    @Transactional
    public MealPlan replaceRecipe(Long mealPlanId, Long recipeId) {
        MealPlan mealPlan = getMealPlanById(mealPlanId);

        int index = -1;
        for (int i = 0; i < mealPlan.getRecipes().size(); i++) {
            if (mealPlan.getRecipes().get(i).getId().equals(recipeId)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new RuntimeException("Recipe not found in meal plan");
        }

        List<SpoonacularRecipeDTO> newRecipes = spoonacularClient.getRandomRecipes(1);
        SpoonacularRecipeDTO newRecipeDTO = newRecipes.get(0);

        Recipe newRecipe = getOrSaveRecipe(newRecipeDTO);

        mealPlan.getRecipes().set(index, newRecipe);

        return mealPlanRepository.save(mealPlan);
    }

}
