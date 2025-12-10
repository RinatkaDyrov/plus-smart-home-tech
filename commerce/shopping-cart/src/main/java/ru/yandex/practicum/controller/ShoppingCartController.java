package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

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
        log.info("Запрос получения корзины. Пользователь={}", username);
        ShoppingCartDto result = cartService.getShoppingCart(username);
        log.debug("Корзина получена: {}", result);
        return result;
    }

    @PutMapping
    public ShoppingCartDto addToCart(
            @RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username,
            @RequestBody Map<UUID, Integer> product
    ) {
        log.info("Добавление товаров в корзину. Пользователь={}, данные={}", username, product);
        ShoppingCartDto result = cartService.add(username, product);
        log.debug("Товары добавлены. Текущая корзина: {}", result);
        return result;
    }

    @DeleteMapping
    public void deleteCart(@RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username) {
        log.info("Удаление корзины пользователя={}", username);
        cartService.deleteCart(username);
        log.info("Корзина пользователя={} успешно удалена", username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto deleteProductsFromCart(
            @RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username,
            @RequestBody List<UUID> productsIds
    ) {
        log.info("Удаление товаров из корзины. Пользователь={}, IDs={}", username, productsIds);
        ShoppingCartDto result = cartService.deleteProducts(username, productsIds);
        log.debug("Товары удалены. Текущая корзина: {}", result);
        return result;
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeQuantityInCart(
            @RequestParam @NotNull(message = "Имя пользователя не должно быть пустым") String username,
            @RequestBody @Valid ChangeProductQuantityRequest request
    ) {
        log.info("Изменение количества товаров. Пользователь={}, запрос={}", username, request);
        ShoppingCartDto result = cartService.changeQuantity(username, request);
        log.debug("Количество изменено. Текущая корзина: {}", result);
        return result;
    }
}