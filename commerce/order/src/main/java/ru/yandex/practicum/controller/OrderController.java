package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getUserOrders(@RequestParam
                                        @NotBlank(message = "Необходимо указать имя пользователя") String username,
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение списка заказов пользователя {}", username);
        return orderService.getUserOrders(username, page, size);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        log.info("Создание нового заказа: {}", request);
        return orderService.createNewOrder(request);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request) {
        log.info("Возврат заказа {}", request);
        return orderService.returnOrder(request);
    }

    @PostMapping("/payment")
    public OrderDto payment(@RequestBody
                            @NotNull(message = "Необходимо указать идентификационный номер заказа")
                            UUID orderId) {
        log.info("Выполняется оплата заказа");
        return orderService.payment(true, orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto paymentFailed(@RequestBody
                                  @NotNull(message = "Необходимо указать идентификационный номер заказа")
                                  UUID orderId) {
        log.info("Оплата заказа не прошла");
        return orderService.payment(false, orderId);
    }

    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody
                             @NotNull(message = "Необходимо указать идентификационный номер заказа")
                             UUID orderId) {
        log.info("Выполняется доставка заказа");
        return orderService.delivery(true, orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto deliveryFailed(@RequestBody
                                   @NotNull(message = "Необходимо указать идентификационный номер заказа")
                                   UUID orderId) {
        log.info("Доставка заказа произошла с ошибкой");
        return orderService.delivery(false, orderId);
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@RequestBody
                                  @NotNull(message = "Необходимо указать идентификационный номер заказа")
                                  UUID orderId) {
        log.info("Завершение заказа");
        return orderService.completeOrder(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalPrice(@RequestBody
                                        @NotNull(message = "Необходимо указать идентификационный номер заказа")
                                        UUID orderId) {
        log.info("Расчёт стоимости заказа");
        return orderService.calculateTotalPrice(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryPrice(@RequestBody
                                           @NotNull(message = "Необходимо указать идентификационный номер заказа")
                                           UUID orderId) {
        log.info("Расчёт стоимости доставки заказа");
        return orderService.calculateDeliveryPrice(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody
                             @NotNull(message = "Необходимо указать идентификационный номер заказа")
                             UUID orderId) {
        log.info("Сборка заказа");
        return orderService.assembly(true, orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody
                             @NotNull(message = "Необходимо указать идентификационный номер заказа")
                             UUID orderId) {
        log.info("Сборка заказа произошла с ошибкой");
        return orderService.assembly(false, orderId);
    }
}
