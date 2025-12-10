package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@Table(
        name = "shopping_cart",
        indexes = {
                @Index(name = "idx_shopping_cart_username", columnList = "username")
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id")
    private UUID shoppingCartId;

    private String username;

    @Enumerated(EnumType.STRING)
    private ShoppingCartStatus status;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ShoppingCartItem> items = new ArrayList<>();
}
