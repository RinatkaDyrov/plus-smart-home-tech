package ru.yandex.practicum.handler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApiError {
    private int status;
    private String error;
    private String message;
    private String userMessage;
    private Object details;
    private List<String> stackTrace;
}
