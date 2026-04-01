package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, Double amount) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.amount = amount;
    }

    //Relacja @ManyToOne z Recipe
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    //Relacja @ManyToOne z Ingredients
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
}
