package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyProductsForOrderRequest {
    @NotNull(message = "Необходимо указать список товаров")
    Map<UUID, Long> products;
    @NotNull(message = "Необходимо указать идентификационный номер заказа")
    UUID orderId;
}
