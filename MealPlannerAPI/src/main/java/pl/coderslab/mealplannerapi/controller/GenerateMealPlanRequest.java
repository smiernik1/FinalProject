package pl.coderslab.mealplannerapi.controller;

import lombok.Getter;
import lombok.Setter;
import pl.coderslab.mealplannerapi.dto.CreateMealPlanRequestDTO;

import java.util.List;

@Getter
@Setter
public class GenerateMealPlanRequest {
    private CreateMealPlanRequestDTO request;
    private List<String> dishTypes;
}
