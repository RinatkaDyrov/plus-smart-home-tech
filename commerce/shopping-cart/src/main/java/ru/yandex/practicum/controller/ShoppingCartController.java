package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(
            @RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username
    ) {
        return cartService.getShoppingCart(username);
    }

    @PutMapping
    public ShoppingCartDto addToCart(
            @RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username,
            @RequestBody Map<UUID, Integer> product
    ) {
        return cartService.add(username, product);
    }

    @DeleteMapping
    public void deleteCart(@RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username) {
        cartService.deleteCart(username);
    }

    @PutMapping("/remove")
    public ShoppingCartDto deleteProductsFromCart(@RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username,
                                                  @RequestBody List<UUID> productsIds) {
        return cartService.deleteProducts(username, productsIds);
    }

    @PutMapping("/change-quantity")
    public ShoppingCartDto changeQuantityInCart(@RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username,
                                                @RequestBody ChangeProductQuantityRequest request) {
        return cartService.changeQuantity(username, request);
    }
}
