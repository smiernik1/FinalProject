package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.coderslab.mealplannerapi.entity.ShoppingListItem;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {
}
