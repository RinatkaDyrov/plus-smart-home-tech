package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PutMapping
    public void addNewProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request) {
        warehouseService.addNewToWarehouse(request);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductsQuantity(@RequestBody @Valid ShoppingCartDto cartDto) {
        return warehouseService.checkProductsFromCart(cartDto);
    }

    @PostMapping("/add")
    public void addProductToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @PostMapping("/shipped")
    public void shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        log.info("Передать товары в доставку");
        warehouseService.shippedToDelivery(request);
    }

    @PostMapping("/return")
    public void returnProducts(@RequestBody
                               @NotNull(message = "Необходимо указать список товаров для оформления возврата")
                               Map<UUID, Integer> products) {
        log.info("Принять возврат товаров на склад");
        warehouseService.returnProducts(products);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProducts(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        log.info("Собрать товары к заказу для подготовки к отправке");
        return warehouseService.assembly(request);
    }
}
