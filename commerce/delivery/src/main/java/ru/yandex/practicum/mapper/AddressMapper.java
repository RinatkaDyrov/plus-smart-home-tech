package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.warehouse.AddressDto;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDto toAddressDto(Address address);

    Address toAddressEntity(AddressDto addressDto);
}
