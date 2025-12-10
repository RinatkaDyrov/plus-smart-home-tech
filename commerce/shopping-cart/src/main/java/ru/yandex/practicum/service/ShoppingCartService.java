package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.cart.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.cart.ShoppingCartNotFoundException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartItem;
import ru.yandex.practicum.model.ShoppingCartStatus;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;

    @Transactional(readOnly = true)
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Получение корзины. Пользователь={}", username);
        ShoppingCart cart = getCartFromDB(username);
        log.debug("Корзина найдена: {}", cart);
        return cartMapper.mapToCartDto(cart);
    }

    public ShoppingCartDto add(String username, Map<UUID, Integer> productsToAdd) {
        log.info("Добавление товаров в корзину. Пользователь={}", username);
        log.debug("Данные для добавления: {}", productsToAdd);

        ShoppingCart cart = cartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseGet(() -> {
                    log.info("Корзина не найдена. Создаю новую корзину для пользователя={}", username);
                    return createNewCart(username);
                });

        if (productsToAdd == null || productsToAdd.isEmpty()) {
            log.info("Товары для добавления отсутствуют. Возвращаю текущую корзину");
            return cartMapper.mapToCartDto(cart);
        }

        Map<UUID, ShoppingCartItem> existingItems = cart.getItems().stream()
                .collect(Collectors.toMap(ShoppingCartItem::getProductId, item -> item));

        for (Map.Entry<UUID, Integer> entry : productsToAdd.entrySet()) {
            UUID productId = entry.getKey();
            int count = entry.getValue();

            log.debug("Обработка товара productId={}, count={}", productId, count);

            if (count == 0) {
                log.debug("Количество = 0, пропуск товара {}", productId);
                continue;
            }

            ShoppingCartItem item = existingItems.get(productId);

            if (item == null) {
                if (count > 0) {
                    log.info("Добавляю новый товар в корзину: productId={}, count={}", productId, count);
                    ShoppingCartItem newItem = ShoppingCartItem.builder()
                            .cart(cart)
                            .productId(productId)
                            .quantity((long) count)
                            .build();
                    cart.getItems().add(newItem);
                }
            } else {
                long newCount = item.getQuantity() + count;
                log.debug("Обновление количества товара productId={}, oldCount={}, newCount={}",
                        productId, item.getQuantity(), newCount);

                if (newCount > 0) {
                    item.setQuantity(newCount);
                } else {
                    log.info("Количество стало ≤ 0. Удаляю товар productId={} из корзины", productId);
                    cart.getItems().remove(item);
                }
            }
        }

        ShoppingCart savedCart = cartRepository.save(cart);
        log.info("Корзина обновлена и сохранена. Пользователь={}", username);
        log.debug("Сохраненная корзина: {}", savedCart);

        return cartMapper.mapToCartDto(savedCart);
    }

    public void deleteCart(String username) {
        log.info("Удаление корзины пользователя={}", username);
        ShoppingCart cartForDeleting = getCartFromDB(username);
        cartForDeleting.setStatus(ShoppingCartStatus.DEACTIVATED);
        log.info("Корзина пользователя={} помечена как деактивированная", username);
    }

    public ShoppingCartDto deleteProducts(String username, List<UUID> productsIds) {
        log.info("Удаление товаров из корзины. Пользователь={}", username);
        log.debug("IDs для удаления: {}", productsIds);

        ShoppingCart cart = getCartFromDB(username);

        if (productsIds == null || productsIds.isEmpty()) {
            log.info("Список товаров для удаления пуст. Корзина не изменена.");
            return cartMapper.mapToCartDto(cart);
        }

        Set<UUID> idsForDelete = new HashSet<>(productsIds);
        cart.getItems().removeIf(item -> idsForDelete.contains(item.getProductId()));

        log.info("Товары удалены из корзины. Пользователь={}", username);

        ShoppingCart saved = cartRepository.save(cart);
        log.debug("Сохраненная корзина после удаления: {}", saved);

        return cartMapper.mapToCartDto(saved);
    }

    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("Изменение количества товара в корзине. Пользователь={}", username);
        log.debug("Запрос изменения: {}", request);

        ShoppingCart cart = getCartFromDB(username);

        ShoppingCartItem changedItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Попытка изменить количество товара, которого нет в корзине: productId={}", request.getProductId());
                    return new NoProductsInShoppingCartException("Товар с таким ID не найден", "Not found");
                });

        Long newQuantity = request.getNewQuantity();

        if (newQuantity == null) {
            log.error("Получено новое количество null");
            throw new IllegalArgumentException("Новое количество не может быть null");
        }

        if (newQuantity <= 0) {
            log.info("Количество <= 0. Удаляю товар productId={} из корзины", request.getProductId());
            cart.getItems().remove(changedItem);
        } else {
            log.info("Обновление количества товара productId={} на {}", request.getProductId(), newQuantity);
            changedItem.setQuantity(newQuantity);
        }

        ShoppingCart saved = cartRepository.save(cart);
        log.debug("Корзина после изменения количества: {}", saved);

        return cartMapper.mapToCartDto(saved);
    }

    private ShoppingCart getCartFromDB(String username) {
        log.debug("Поиск корзины в БД. Пользователь={}", username);

        return cartRepository.findByUsernameAndStatus(username, ShoppingCartStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Корзина пользователя={} не найдена", username);
                    return new ShoppingCartNotFoundException(
                            "Корзина пользователя " + username + " не найдена",
                            "Корзина не найдена"
                    );
                });
    }

    private ShoppingCart createNewCart(String username) {
        log.info("Создание новой корзины для пользователя={}", username);
        ShoppingCart newCart = ShoppingCart.builder()
                .username(username)
                .status(ShoppingCartStatus.ACTIVE)
                .build();
        return cartRepository.save(newCart);
    }
}
