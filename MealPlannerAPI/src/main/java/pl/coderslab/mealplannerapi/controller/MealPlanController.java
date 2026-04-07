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
    public MealPlan generateMealPlan(@Valid @RequestBody CreateMealPlanRequestDTO request) {
        return mealPlanService.generateMealPlan(request);
    }

    @GetMapping("/get/all")
    public List<MealPlan> getAllMealPlans() {
        return mealPlanService.getAllMealPlans();
    }

    @GetMapping("/get/{id}")
    public MealPlan getMEalPlansById(@PathVariable Long id) {
        return mealPlanService.getMealPlanById(id);
    }

    @GetMapping("/get/shopping-list/{id}")
    public List<ShoppingListItemDTO> getShoppingList(@PathVariable Long id) {
        return mealPlanService.getShoppingList(id);
    }
}
