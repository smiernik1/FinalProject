package pl.coderslab.mealplannerapi.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.mealplannerapi.dto.ShoppingListResponseDTO;
import pl.coderslab.mealplannerapi.dto.UpdateShoppingListRequestDTO;
import pl.coderslab.mealplannerapi.service.ShoppingListService;

@RestController
@RequestMapping("/api")
public class ShoppingListController {
    private final ShoppingListService shoppingListService;

    public ShoppingListController( ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @PostMapping("/meal-plans/{id}/shopping-list")
    public ShoppingListResponseDTO createShoppingListForMealPlan(@PathVariable Long id) {
        return shoppingListService.getOrCreateShoppingList(id);
    }

    @GetMapping("/shopping-lists/{id}")
    public ShoppingListResponseDTO getShoppingListById(@PathVariable Long id) {
        return shoppingListService.getShoppingListById(id);
    }

    @PutMapping("/shopping-lists/{id}")
    public ShoppingListResponseDTO updateShoppingList(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShoppingListRequestDTO request) {
        return shoppingListService.updateShoppingList(id, request);
    }

    @DeleteMapping("/shopping-lists/{id}")
    public void deleteShoppingList(@PathVariable Long id) {
        shoppingListService.deleteShoppingList(id);
    }
}
