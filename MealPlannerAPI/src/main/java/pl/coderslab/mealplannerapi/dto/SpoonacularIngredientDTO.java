package pl.coderslab.mealplannerapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularIngredientDTO {

    private Long id;
    private String name;
    private BigDecimal amount;
    private String unit;

}
