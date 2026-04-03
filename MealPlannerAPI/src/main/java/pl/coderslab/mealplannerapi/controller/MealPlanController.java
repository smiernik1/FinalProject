package pl.coderslab.mealplannerapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderslab.mealplannerapi.dto.CreateMealPlanRequestDTO;
import pl.coderslab.mealplannerapi.entity.MealPlan;
import pl.coderslab.mealplannerapi.service.MealPlanService;

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
}
