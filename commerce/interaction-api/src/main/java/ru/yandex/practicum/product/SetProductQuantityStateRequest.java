package ru.yandex.practicum.product;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class SetProductQuantityStateRequest {
    @NotNull(message = "Необходимо указать идентификационный номер товара")
    private UUID productId;
    @NotNull(message = "Необходимо указать статус состояния остатка товара")
    private QuantityState quantityState;
}