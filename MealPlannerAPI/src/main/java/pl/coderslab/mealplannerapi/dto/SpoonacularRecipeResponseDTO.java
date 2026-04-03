package pl.coderslab.mealplannerapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SpoonacularRecipeResponseDTO {
    private List<SpoonacularRecipeDTO> recipes;
}
