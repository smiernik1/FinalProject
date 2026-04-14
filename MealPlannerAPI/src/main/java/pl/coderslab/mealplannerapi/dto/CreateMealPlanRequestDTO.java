package pl.coderslab.mealplannerapi.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.coderslab.mealplannerapi.entity.MealPlan;
import pl.coderslab.mealplannerapi.entity.MealPlanRecipe;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateMealPlanRequestDTO {
    @NotNull(message = "daysCount is required")
    @Min(value = 1, message = "daysCount must be at least 1")
    @Max(value = 5, message = "daysCount must be at most 5")
    private Integer daysCount;
    @NotNull(message = "mealPerDay is required")
    @Min(value = 1, message = "mealPerDay must be at least 1")
    @Max(value = 14, message = "maelPerDay must be at most 8")
    private Integer mealPerDay;

    private String diet;
    @Min(value = 0, message = "minCalories must be at least 0")
    private Integer minCalories;
    @Min(value = 10, message = "maxCalories must be at least 10")
    private Integer maxCalories;
}