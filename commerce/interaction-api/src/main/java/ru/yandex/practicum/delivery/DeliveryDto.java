package ru.yandex.practicum.delivery;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.warehouse.AddressDto;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryDto {

    UUID deliveryId;

    AddressDto fromAddress;

    AddressDto toAddress;

    UUID orderId;

    DeliveryStateDto deliveryState;
}