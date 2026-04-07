package pl.coderslab.mealplannerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListResponseDTO {
    private Long id;
    private Long mealPlanId;
    private List<ShoppingListItemDTO> items = new ArrayList<>();

}
