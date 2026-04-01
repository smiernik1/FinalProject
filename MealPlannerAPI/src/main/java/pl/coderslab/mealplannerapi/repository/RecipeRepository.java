package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.mealplannerapi.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe,Long> {
}
