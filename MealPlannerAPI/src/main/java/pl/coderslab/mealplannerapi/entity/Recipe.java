package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="recipes")
@Getter
@Setter
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer calories;

    public Recipe(String name, Integer calories) {
        this.name = name;
        this.calories = calories;
    }

    //Relacja @ManyToMany z Category
    @ManyToMany(mappedBy = "recipes")
    private Set<Category> categories = new HashSet<>();

    //Relacja @OneToMany z RecipeIngredient
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> ingredients = new HashSet<>();
}
