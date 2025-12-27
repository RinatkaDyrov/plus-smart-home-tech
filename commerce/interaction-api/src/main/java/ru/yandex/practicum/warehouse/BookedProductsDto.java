package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class BookedProductsDto {
    @NotNull(message = "Необходимо указать общий вес доставки")
    BigDecimal deliveryWeight;
    @NotNull(message = "Необходимо указать общий объем доставки")
    BigDecimal deliveryVolume;
    @NotNull(message = "Необходимо указать есть ли в доставке хрупкие вещи")
    Boolean fragile;
}