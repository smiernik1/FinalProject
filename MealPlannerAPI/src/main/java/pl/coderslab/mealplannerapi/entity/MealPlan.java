package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meal_plans")
@Getter
@Setter
@NoArgsConstructor
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private Integer daysCount;

    public MealPlan(LocalDate startDate, Integer daysCount, Set<Recipe> recipes) {
        this.startDate = startDate;
        this.daysCount = daysCount;
        this.recipes = recipes;
    }

    //Relacja @ManyToMany z Recipe
    @ManyToMany
    @JoinTable(
            name = "mealplan_recipe",
            joinColumns = @JoinColumn(name = "mealplan_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> recipes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
