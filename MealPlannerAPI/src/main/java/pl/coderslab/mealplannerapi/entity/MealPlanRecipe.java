package pl.coderslab.mealplannerapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mealplan_recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relacja do MealPlan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mealplan_id", nullable = false)
    @JsonIgnore
    private MealPlan mealPlan;

    // relacja do Recipe
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    // dzień przypisania
    @Column(nullable = false)
    private int day;

    private String dishType;
}
