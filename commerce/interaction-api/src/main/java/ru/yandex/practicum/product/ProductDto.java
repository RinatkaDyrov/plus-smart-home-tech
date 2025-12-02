package ru.yandex.practicum.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ProductDto {
    String productId;
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
    @Min(value = 1, message = "Минимальная стоимость товара 1 у.е.")
    double price;
}
