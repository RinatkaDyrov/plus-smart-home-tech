package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NewProductInWarehouseRequest {
    @NotNull(message = "Необходимо указать идентификационный номер товара")
    UUID productId;

    Boolean fragile;

    @NotNull(message = "Необходимо указать размеры товара")
    DimensionDto dimension;

    @NotNull(message = "Необходимо указать вес товара")
    @Min(value = 1)
    Double weight;
}
