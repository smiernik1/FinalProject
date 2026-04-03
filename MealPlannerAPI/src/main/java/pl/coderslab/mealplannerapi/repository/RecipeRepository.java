package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.coderslab.mealplannerapi.entity.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe,Long> {

    Optional<Recipe> findByExternalId(Long externalId);

//    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c.name = :name")
//    List<Recipe> findByCategoryName(@Param("name") String categoryName);
//
//    //List<Recipe> findByCaloriesBetween(int min, int max);
//
//    Optional<Recipe> findByName(String name);
}
