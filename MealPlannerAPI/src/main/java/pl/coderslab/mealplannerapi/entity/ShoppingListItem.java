package pl.coderslab.mealplannerapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name ="shopping_list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String unit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="shopping_list_id")
    @JsonIgnore
    private ShoppingList shoppingList;
}
