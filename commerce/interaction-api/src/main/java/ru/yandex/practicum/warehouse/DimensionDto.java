package ru.yandex.practicum.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class DimensionDto {
    @NotNull(message = "Необходимо указать ширину товара")
    @Min(value = 1)
    Double width;
    @NotNull(message = "Необходимо указать высоту товара")
    @Min(value = 1)
    Double height;
    @NotNull(message = "Необходимо указать глубину товара")
    @Min(value = 1)
    Double depth;
}
