package pl.coderslab.mealplannerapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    public User(String email) {
        this.email = email;
    }

    //Relacja @OneToMany z MealPlan
    @OneToMany(mappedBy ="user",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MealPlan> mealPlans = new HashSet<>();
}
