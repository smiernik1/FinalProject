package pl.coderslab.mealplannerapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.mealplannerapi.dto.CreateMealPlanRequestDTO;
import pl.coderslab.mealplannerapi.dto.ShoppingListItemDTO;
import pl.coderslab.mealplannerapi.entity.MealPlan;
import pl.coderslab.mealplannerapi.service.MealPlanService;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;
    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping("/generate")
    public MealPlan generateMealPlan(@Valid @RequestBody GenerateMealPlanRequest body) {
        return mealPlanService.generateMealPlan(body.getRequest(), body.getDishTypes());
    }

    @GetMapping("/get/all")
    public List<MealPlan> getAllMealPlans() {
        return mealPlanService.getAllMealPlans();
    }

    @GetMapping("/get/{id}")
    public MealPlan getMEalPlansById(@PathVariable Long id) {
        return mealPlanService.getMealPlanById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMealPlanById(@PathVariable Long id) {
        mealPlanService.deleteMealPlanById(id);
    }

    @PostMapping("/{mealPlanId}/replace-recipe/{recipeId}")
    public MealPlan replaceRecipe(
            @PathVariable Long mealPlanId,
            @PathVariable Long recipeId
    ) {
        return mealPlanService.replaceRecipe(mealPlanId, recipeId);
    }
}
