//package pl.coderslab.mealplannerapi.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//@Table(name="categories")
//@Getter
//@Setter
//@NoArgsConstructor
//public class Category {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//
//    //Relacja @OneToMany z RecipeIngredient
//    @ManyToMany
//    @JoinTable(
//            name = "category_recipe",
//            joinColumns = @JoinColumn(name = "category_id"),
//            inverseJoinColumns = @JoinColumn(name = "recipe_id")
//    )
//    private Set<Recipe> recipes = new HashSet<>();
//
//    public Category(String name) {
//        this.name = name;
//    }
//}
