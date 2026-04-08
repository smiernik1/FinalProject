package pl.coderslab.mealplannerapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularRecipeDTO {
    private Long id;
    private String title;
    String image;
    String sourceUrl;
    Integer readyInMinutes;
    Integer servings;

//    Boolean vegetarian;
//    Boolean vegan;
//    Boolean glutenFree;
//    Boolean dairyFree;

    List<SpoonacularIngredientDTO> extendedIngredients;


}




