package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //Relacja @ManyToMany z Recipe
    @ManyToMany
    @JoinTable(
            name = "mealplan_recipe",
            joinColumns = @JoinColumn(name = "mealplan_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> recipes = new ArrayList<>();

//    //Relacja @ManyToOne z User
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
}
