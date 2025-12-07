package ru.yandex.practicum.exception.warehouse;

import lombok.Getter;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    private final String userMessage;

    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}
