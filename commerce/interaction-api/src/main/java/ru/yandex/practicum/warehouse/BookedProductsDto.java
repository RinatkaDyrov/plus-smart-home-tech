package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class BookedProductsDto {
    @NotNull(message = "Необходимо указать общий вес доставки")
    Double deliveryWeight;
    @NotNull(message = "Необходимо указать общий объем доставки")
    Double deliveryVolume;
    @NotNull(message = "Необходимо указать есть ли в доставке хрупкие вещи")
    Boolean fragile;
}