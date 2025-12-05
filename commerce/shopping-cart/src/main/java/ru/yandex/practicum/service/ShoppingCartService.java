package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartService {

    private final ShoppingCartRepository cartRepository;


    public ShoppingCartDto getShoppingCart(String username) {
    }

    public ShoppingCartDto add(String username, Map<UUID, Integer> product) {
    }

    public void deleteCart(String username) {
    }

    public ShoppingCartDto deleteProducts(String username, List<UUID> productsIds) {
    }

    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
    }
}
