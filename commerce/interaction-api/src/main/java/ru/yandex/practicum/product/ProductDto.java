package ru.yandex.practicum.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ProductDto {
    UUID productId;
    @NotNull(message = "Необходимо указать наименование товара ")
    String productName;
    @NotNull(message = "Необходимо указать описание товара")
    String description;
    String imageSrc;
    @NotNull(message = "Необходимо указать статус состояния остатка товара")
    QuantityState quantityState;
    @NotNull(message = "Необходимо указать статус товара")
    ProductState productState;
    @NotNull(message = "Необходимо указать категорию товара")
    ProductCategory productCategory;
    @NotNull(message = "Необходимо указать стоимость товара")
    @DecimalMin(value = "1.00", message = "Минимальная стоимость товара 1 у.е.")
    BigDecimal price;
}
