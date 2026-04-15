package pl.coderslab.mealplannerapi.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import pl.coderslab.mealplannerapi.entity.MealPlan;
import pl.coderslab.mealplannerapi.entity.MealPlanRecipe;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMealPlanRequestDTO {
    @NotNull(message = "daysCount is required")
    @Min(value = 1, message = "daysCount must be at least 1")
    @Max(value = 5, message = "daysCount must be at most 5")
    private Integer daysCount;

    @NotNull(message = "mealPerDay is required")
    @Min(value = 1, message = "mealPerDay must be at least 1")
    @Max(value = 8, message = "mealPerDay must be at most 8")
    private Integer mealPerDay;

    private String diet;

    @Positive(message = "minCalories must be positive")
    private Integer minCalories;

    @Positive(message = "maxCalories must be positive")
    private Integer maxCalories;
}