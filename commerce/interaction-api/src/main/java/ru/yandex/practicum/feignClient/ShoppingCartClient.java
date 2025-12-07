package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getCart(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProduct(
            @RequestParam String username,
            @RequestBody Map<UUID, Long> newProduct
    );

    @DeleteMapping("/api/v1/shopping-cart")
    void deleteCart(@RequestParam String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto deleteProductFromCart(
            @RequestParam String username,
            @RequestBody List<UUID> productIds
    );

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest productQuantityRequest
    );
}
