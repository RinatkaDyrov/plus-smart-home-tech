package ru.yandex.practicum.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    @NotNull(message = "Необходимо указать идентификационный номер товара")
    private UUID productId;
    @NotNull(message = "Необходимо указать новое значение количества товара")
    private Long newQuantity;
}
