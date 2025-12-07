package ru.yandex.practicum.exception.warehouse;

import lombok.Getter;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    private final String userMessage;

    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
