package ru.yandex.practicum.handler;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exception.ApiError;
import ru.yandex.practicum.exception.cart.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.cart.NotAuthorizedUserException;
import ru.yandex.practicum.exception.cart.ShoppingCartNotFoundException;
import ru.yandex.practicum.exception.product.ProductNotFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ShoppingCartExceptionHandler {

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoProductsInShoppingCart(ProductNotFoundException ex) {

        List<String> stack = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(400)
                .error("BAD_REQUEST")
                .message(ex.getMessage())
                .userMessage(ex.getUserMessage())
                .stackTrace(stack)
                .build();
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleProductNotAuthorizedUser(ProductNotFoundException ex) {

        List<String> stack = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(401)
                .error("UNAUTHORIZED")
                .message(ex.getMessage())
                .userMessage(ex.getUserMessage())
                .stackTrace(stack)
                .build();
    }


    @ExceptionHandler(ShoppingCartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleShoppingCartNotFound(ShoppingCartNotFoundException ex) {

        List<String> stack = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());

        return ApiError.builder()
                .status(404)
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .userMessage(ex.getUserMessage())
                .stackTrace(stack)
                .build();
    }

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
}
