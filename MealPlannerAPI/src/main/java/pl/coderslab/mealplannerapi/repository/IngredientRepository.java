package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.mealplannerapi.entity.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {
}
