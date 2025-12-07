package ru.yandex.practicum.exception.cart;

import lombok.Getter;

@Getter
public class ShoppingCartNotFoundException extends RuntimeException {

    private final String userMessage;

    public ShoppingCartNotFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
