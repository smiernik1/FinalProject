package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String category;

    public Recipe(String name, Integer calories, String category) {
        this.name = name;
        this.calories = calories;
        this.category = category;
    }
}
