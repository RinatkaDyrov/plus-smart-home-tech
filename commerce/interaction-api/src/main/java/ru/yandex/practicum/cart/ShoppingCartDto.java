package ru.yandex.practicum.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartDto {
    UUID shoppingCartId;

    Map<UUID, Long> products;
}
