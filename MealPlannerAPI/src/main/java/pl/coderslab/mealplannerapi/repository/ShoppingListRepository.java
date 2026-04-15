package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.mealplannerapi.entity.ShoppingList;

import java.util.Optional;

@Repository
public interface ShoppingListRepository  extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findByMealPlanId(Long mealPlanId);
}
