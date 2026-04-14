package pl.coderslab.mealplannerapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularRecipeDTO {
    private Long id;
    private String title;
    private String image;
    private String sourceUrl;
    private Integer readyInMinutes;
    private Integer servings;

    private NutritionDTO nutrition;

    private List<SpoonacularIngredientDTO> extendedIngredients =  new ArrayList<>();
}




