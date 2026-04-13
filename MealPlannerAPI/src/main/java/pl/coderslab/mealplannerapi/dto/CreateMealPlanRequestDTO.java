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
    @Max(value = 14, message = "daysCount must be at most 14")
    private Integer daysCount;
    private Integer mealPerDay;

    private String diet;
}