package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.OrderDto;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order mapToOrder(OrderDto orderDto);
    OrderDto mapToOrderDto(Order order);
}
