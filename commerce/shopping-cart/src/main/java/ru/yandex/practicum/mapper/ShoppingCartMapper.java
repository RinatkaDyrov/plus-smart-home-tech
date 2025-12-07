package ru.yandex.practicum.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartItem;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "shoppingCartId", target = "shoppingCartId")
    @Mapping(source = "items", target = "products")
    ShoppingCartDto mapToCartDto(ShoppingCart cart);

    default Map<UUID, Long> mapProducts(List<ShoppingCartItem> items) {
        if (items == null || items.isEmpty()) {
            return Map.of();
        }

        return items.stream()
                .collect(Collectors.toMap(
                        ShoppingCartItem::getProductId,
                        ShoppingCartItem::getQuantity,
                        (q1, q2) -> q1
                ));
    }


}
