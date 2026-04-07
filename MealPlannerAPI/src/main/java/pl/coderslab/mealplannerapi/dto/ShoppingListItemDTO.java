package pl.coderslab.mealplannerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemDTO {
    private Long id;
    private String ingredientName;
    private BigDecimal amount;
    private String unit;

    public ShoppingListItemDTO(String ingredientName, BigDecimal amount, String unit) {
    }

    public ShoppingListItemDTO(Long id, BigDecimal amount, String ingredientName, String unit) {
    }
}
