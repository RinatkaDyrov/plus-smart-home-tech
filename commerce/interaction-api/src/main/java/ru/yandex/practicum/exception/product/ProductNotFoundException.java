package ru.yandex.practicum.exception.product;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {

    private final String userMessage;

    public ProductNotFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

}
