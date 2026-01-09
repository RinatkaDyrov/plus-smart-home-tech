package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippedToDeliveryRequest {
    @NotNull(message = "Необходимо указать идентификационный номер заказа")
    UUID orderId;
    @NotNull(message = "Необходимо указать идентификационный номер доставки")
    UUID deliveryId;
}
