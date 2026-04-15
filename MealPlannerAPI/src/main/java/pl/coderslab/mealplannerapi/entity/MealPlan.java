package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private Integer daysCount;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPlanRecipe> mealPlanRecipes =  new ArrayList<>();

    public void addRecipe(Recipe recipe, int day, String dishType) {
        MealPlanRecipe mpr = new MealPlanRecipe();
        mpr.setMealPlan(this);
        mpr.setRecipe(recipe);
        mpr.setDay(day);
        mpr.setDishType(dishType);

        mealPlanRecipes.add(mpr);
    }

    @Column(nullable = false)
    private boolean shoppingListGenerated = false;

    //private String diet;
    @Enumerated(EnumType.STRING)
    private Diet diet;
    private Integer minCalories;
    private Integer maxCalories;
}
