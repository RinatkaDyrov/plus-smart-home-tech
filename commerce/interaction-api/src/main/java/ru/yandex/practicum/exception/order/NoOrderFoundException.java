package ru.yandex.practicum.exception.order;

import lombok.Getter;

@Getter
public class NoOrderFoundException extends RuntimeException{
    private final String userMessage;

    public NoOrderFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
