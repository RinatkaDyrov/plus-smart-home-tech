package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "WAREHOUSE")
public interface WarehouseClient {

    @PutMapping("/api/v1/warehouse")
    void createProduct(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkProductState(@RequestBody ShoppingCartDto cartDto);

    @PostMapping("/api/v1/warehouse/add")
    void addQuantityProductToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getCurrentWarehouseAddress();

    @PostMapping("/api/v1/warehouse/return")
    void returnedProduct(@RequestBody Map<UUID, Long> returnedProducts);

    @PostMapping("/api/v1/warehouse/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest);
}
