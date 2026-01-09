package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private UUID shoppingCartId;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private boolean fragile;

    @ElementCollection
    @CollectionTable(name = "booking_products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    private UUID orderId;
    private UUID deliveryId;
}
