package pl.coderslab.mealplannerapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.mealplannerapi.dto.ShoppingListItemDTO;
import pl.coderslab.mealplannerapi.dto.ShoppingListResponseDTO;
import pl.coderslab.mealplannerapi.dto.UpdateShoppingListRequestDTO;
import pl.coderslab.mealplannerapi.entity.*;
import pl.coderslab.mealplannerapi.repository.MealPlanRepository;
import pl.coderslab.mealplannerapi.repository.ShoppingListRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingListService {
    private final ShoppingListRepository shoppingListRepository;
    private final MealPlanRepository mealPlanRepository;

    public ShoppingListService(ShoppingListRepository shoppingListRepository, MealPlanRepository mealPlanRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.mealPlanRepository = mealPlanRepository;
    }


    @Transactional
    public ShoppingListResponseDTO getOrCreateShoppingList(Long mealPlanId) {
        ShoppingList shoppingList = shoppingListRepository.findByMealPlanId(mealPlanId)
                .orElseGet(() -> {
                    MealPlan mealPlan = mealPlanRepository.findById(mealPlanId)
                            .orElseThrow(() -> new RuntimeException("Can't find mealPlan with id: " + mealPlanId));

                    return createShoppingList(mealPlan);
                });
        return mapToDto(shoppingList);
    }

    @Transactional
    protected ShoppingList createShoppingList(MealPlan mealPlan) {
        Map<String, BigDecimal> totals = new LinkedHashMap<>();

        for (MealPlanRecipe mealPlanRecipe : mealPlan.getMealPlanRecipes()) {
            Recipe recipe = mealPlanRecipe.getRecipe();
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                String ingredientName = recipeIngredient.getIngredient().getName();
                String unit = recipeIngredient.getUnit();
                BigDecimal amount = recipeIngredient.getAmount();

                String key = ingredientName + ", " + unit;
                totals.put(key, totals.getOrDefault(key, BigDecimal.ZERO).add(amount));
            }
        }

        ShoppingList shoppingList = ShoppingList.builder()
                .mealPlan(mealPlan)
                .createdAt(LocalDateTime.now())
                .build();

        for (Map.Entry<String, BigDecimal> entry : totals.entrySet()) {
            String[] parts = entry.getKey().split(", ");
            String ingredientName = parts[0];
            String unit = parts[1];
            BigDecimal amount = entry.getValue();

            ShoppingListItem shoppingListItem = ShoppingListItem.builder()
                    .name(ingredientName)
                    .amount(amount)
                    .unit(unit)
                    .build();

            shoppingList.addItem(shoppingListItem);
        }
        mealPlan.setShoppingListGenerated(true);
        return shoppingListRepository.save(shoppingList);
    }

    public ShoppingListResponseDTO getShoppingListById(Long Id) {
        ShoppingList shoppingList = shoppingListRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Can't find shoppingList with id: " + Id));
        return mapToDto(shoppingList);
    }

    private ShoppingListResponseDTO mapToDto(ShoppingList shoppingList) {
        List<ShoppingListItemDTO> itemDto = shoppingList.getItems().stream()
                .map(item -> new ShoppingListItemDTO(
                        item.getId(),
                        item.getName(),
                        item.getAmount(),
                        item.getUnit()
                ))
                .toList();

        return new ShoppingListResponseDTO(
                shoppingList.getId(),
                shoppingList.getMealPlan().getId(),
                itemDto
        );
    }

    @Transactional
    public ShoppingListResponseDTO updateShoppingList(Long Id, UpdateShoppingListRequestDTO request) {
        ShoppingList shoppingList = shoppingListRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("Can't find shoppingList with id: " + Id));

        shoppingList.getItems().clear();

        for (ShoppingListItemDTO item : request.getItems()) {
            ShoppingListItem shoppingListItem = ShoppingListItem.builder()
                    .name(item.getName())
                    .amount(item.getAmount())
                    .unit(item.getUnit())
                    .build();
            shoppingList.addItem(shoppingListItem);
        }

        ShoppingList shoppingListUpdated = shoppingListRepository.save(shoppingList);

        return mapToDto(shoppingListUpdated);
    }

    public void deleteShoppingList(Long id) {
        ShoppingList shoppingList = shoppingListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can't find shoppingList with id: " + id));

        MealPlan mealPlan = shoppingList.getMealPlan();
        if (mealPlan != null) {
            mealPlan.setShoppingListGenerated(false);
            mealPlanRepository.save(mealPlan);
            shoppingListRepository.deleteById(id);
        }
    }
}
