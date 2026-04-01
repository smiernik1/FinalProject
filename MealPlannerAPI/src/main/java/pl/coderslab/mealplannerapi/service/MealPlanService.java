package pl.coderslab.mealplannerapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderslab.mealplannerapi.dto.MealPlanRequestDTO;
import pl.coderslab.mealplannerapi.entity.MealPlan;
import pl.coderslab.mealplannerapi.entity.Recipe;
import pl.coderslab.mealplannerapi.repository.MealPlanRepository;
import pl.coderslab.mealplannerapi.repository.RecipeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealPlanService {

    RecipeRepository recipeRepository;
    MealPlanRepository mealPlanRepository;

    public MealPlan generateMealPlan(MealPlanRequestDTO mealPlanRequestDTO) {
        List<Recipe> recipes;

        if (mealPlanRequestDTO.getCategory() != null && !mealPlanRequestDTO.getCategory().isEmpty()) {
            recipes = recipeRepository.findByCategoryName(mealPlanRequestDTO.getCategory());
        } else {
            recipes = recipeRepository.findAll();
        }

        if (mealPlanRequestDTO.getMinCalories() != null && mealPlanRequestDTO.getMaxCalories() != null &&
                mealPlanRequestDTO.getMinCalories() > mealPlanRequestDTO.getMaxCalories()) {
            throw new RuntimeException("Min calories cannot be greater than max calories");
        }

        if (mealPlanRequestDTO.getMinCalories() != null && mealPlanRequestDTO.getMaxCalories() != null) {
            recipes = recipes.stream()
                    .filter(r -> r.getCalories() >= mealPlanRequestDTO.getMinCalories()
                            && r.getCalories() <= mealPlanRequestDTO.getMaxCalories())
                    .toList();
        }

        Collections.shuffle(recipes);

        if (recipes.size() < mealPlanRequestDTO.getDays()) {
            throw new RuntimeException("Nie ma wystarczającej liczby przepisów spełniających podane warunki :(");
        }

        Set<Recipe> selectedRecipes = recipes.stream()
                .limit(mealPlanRequestDTO.getDays())
                .collect(Collectors.toSet());

        MealPlan mealPlan = new MealPlan();
        mealPlan.setRecipes(selectedRecipes);
        mealPlan.setDaysCount(mealPlanRequestDTO.getDays());
        return mealPlanRepository.save(mealPlan);
    }
}
