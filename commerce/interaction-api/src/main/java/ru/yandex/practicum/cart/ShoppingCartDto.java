package ru.yandex.practicum.cart;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ShoppingCartDto {
    UUID shoppingCartId;

    Map<UUID, Long> products;
}
