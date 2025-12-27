package ru.yandex.practicum.order;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ProductReturnRequest {
    UUID orderId;
    @NotNull
    Map<UUID, Long> products;
}
