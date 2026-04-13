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
    private LocalDate startDate;
    private Integer daysCount;
//    private Integer minCalories = 0;
//    private Integer maxCalories = Integer.MAX_VALUE;

    //Relacja @ManyToMany z Recipe
//    @ManyToMany
//    @JoinTable(
//            name = "mealplan_recipe",
//            joinColumns = @JoinColumn(name = "mealplan_id"),
//            inverseJoinColumns = @JoinColumn(name = "recipe_id")
//    )
//    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonIgnore
    private List<MealPlanRecipe> mealPlanRecipes =  new ArrayList<>();

    public void addRecipe(Recipe recipe, int day, String dishType) {
        MealPlanRecipe mpr = new MealPlanRecipe();
        mpr.setMealPlan(this);
        mpr.setRecipe(recipe);
        mpr.setDay(day);
        mpr.setDishType(dishType);

        mealPlanRecipes.add(mpr);
    }

    private boolean shoppingListGenerated;

    private String diet;
}
