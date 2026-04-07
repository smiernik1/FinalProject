package pl.coderslab.mealplannerapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateShoppingListRequestDTO {

    @NotNull(message = "items are required")
    @Valid
    private List<ShoppingListItemDTO> items =  new ArrayList<>();

}
