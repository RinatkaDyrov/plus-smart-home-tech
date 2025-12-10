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
public class AddProductToWarehouseRequest {
    UUID productId;

    @NotNull(message = "Необходимо указать количество единиц товара для добавления на склад")
    @Min(value = 1)
    Long quantity;
}
