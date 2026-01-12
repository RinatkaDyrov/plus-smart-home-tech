package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.feignClient.DeliveryClient;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController implements DeliveryClient {
    private final DeliveryService service;

    @PutMapping
    public DeliveryDto createOrder(@Valid @RequestBody DeliveryDto deliveryDto) {
        log.info("Запрос на создание доставки {}", deliveryDto);
        return service.createOrder(deliveryDto);
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID deliveryId) {
        log.info("Смена статуса заказа c id {} на \"Успешно выполнен\"", deliveryId);
        service.successfulDelivery(deliveryId);
    }

    @PostMapping("/picked")
    public void pickedDelivery(@RequestBody UUID deliveryId) {
        log.info("Смена статуса заказа c id {} на \"Принято в работу\"", deliveryId);
        service.pickedDelivery(deliveryId);
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID deliveryId) {
        log.info("Смена статуса заказа c id {} на \"Неудачная доставка\"", deliveryId);
        service.failedDelivery(deliveryId);
    }

    @PostMapping("/cost")
    public BigDecimal calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Расчет стоимости заказа {}", orderDto);
        return service.calculateDeliveryCost(orderDto);
    }
}
