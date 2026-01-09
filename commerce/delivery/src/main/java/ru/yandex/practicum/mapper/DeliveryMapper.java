package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.model.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    Delivery toDelivery(DeliveryDto deliveryDto);

    DeliveryDto toDeliveryDto(Delivery delivery);
}