package pl.coderslab.mealplannerapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateMealPlanRequestDTO {
    @NotNull(message = "daysCount is required")
    @Min(value = 1, message = "daysCount must be at least 1")
    @Max(value = 14, message = "daysCount must be at most 14")
    private Integer daysCount;

    private String diet;
    //    private Integer minCalories;
    //    private Integer maxCalories;
    //    private String category;
}
