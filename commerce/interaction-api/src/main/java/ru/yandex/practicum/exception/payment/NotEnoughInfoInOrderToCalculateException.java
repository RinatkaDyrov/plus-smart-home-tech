package ru.yandex.practicum.exception.payment;

import lombok.Getter;

@Getter
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException{
    private final String userMessage;

    public NotEnoughInfoInOrderToCalculateException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
