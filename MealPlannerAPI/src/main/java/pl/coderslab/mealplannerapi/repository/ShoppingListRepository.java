package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.mealplannerapi.entity.ShoppingList;

import java.util.Optional;

public interface ShoppingListRepository  extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findByMealPlanId(Long melaPlanId);
}
