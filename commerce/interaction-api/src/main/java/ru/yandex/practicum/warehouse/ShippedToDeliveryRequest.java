package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ShippedToDeliveryRequest {
    @NotNull(message = "Необходимо указать идентификационный номер заказа")
    UUID orderId;
    @NotNull(message = "Необходимо указать идентификационный номер доставки")
    UUID deliveryId;
}
