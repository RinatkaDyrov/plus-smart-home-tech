package ru.yandex.practicum.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class WarehouseExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ApiError.builder()
                .status(400)
                .error("BAD_REQUEST")
                .message(ex.getMessage())
                .details(errors)
                .build();
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoSpecifiedProductInWarehouseException(NoSpecifiedProductInWarehouseException ex) {

        List<String> stack = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(400)
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .userMessage(ex.getUserMessage())
                .stackTrace(stack)
                .build();
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleSpecifiedProductAlreadyInWarehouseException(SpecifiedProductAlreadyInWarehouseException ex) {

        List<String> stack = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(400)
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .userMessage(ex.getUserMessage())
                .stackTrace(stack)
                .build();
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouse ex) {

        List<String> stack = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(400)
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .userMessage(ex.getUserMessage())
                .stackTrace(stack)
                .build();
    }
}
