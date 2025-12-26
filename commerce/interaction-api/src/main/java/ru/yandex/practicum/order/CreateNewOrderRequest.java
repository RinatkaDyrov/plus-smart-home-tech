package ru.yandex.practicum.order;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.AddressDto;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNewOrderRequest {
    @NotNull(message = "Необходимо указать корзину для заказа")
    ShoppingCartDto shoppingCart;
    @NotNull(message = "Необходимо указать адресс доставки")
    AddressDto deliveryAddress;
}
