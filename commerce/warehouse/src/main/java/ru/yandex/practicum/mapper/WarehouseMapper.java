package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.model.WarehouseAddress;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "width", source = "dimension.width")
    @Mapping(target = "height", source = "dimension.height")
    @Mapping(target = "depth", source = "dimension.depth")
    @Mapping(target = "quantity", constant = "0L")
    WarehouseProduct mapToProduct(NewProductInWarehouseRequest dto);

    WarehouseAddress mapToAddress(AddressDto dto);

    AddressDto mapToAddressDto(WarehouseAddress address);

    BookedProductsDto toBookedProductsDto(Double deliveryWeight, Double deliveryVolume, Boolean fragile);
}
