package pl.coderslab.mealplannerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.coderslab.mealplannerapi.entity.MealPlan;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan,Long> {
}
