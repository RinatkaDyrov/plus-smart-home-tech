package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feignClient.PaymentClient;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentClient {

    private final PaymentService paymentService;


    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        log.info("Формирование оплаты для заказа: {}", orderDto);
        return paymentService.create(orderDto);
    }

    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Расчёт полной стоимости заказа: {}", orderDto);
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void refundOrder(@RequestBody UUID paymentId) {
        log.info("Метод для эмуляции успешной оплаты в платежного шлюза для заказа (ID: {})", paymentId);
        paymentService.refundOrder(paymentId);
    }

    @PostMapping("/productCost")
    public BigDecimal calculateOrderTotalCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Расчёт стоимости товаров в заказе: {}", orderDto);
        return paymentService.calculateOrderTotalCost(orderDto);
    }

    @PostMapping("/failed")
    public void emulateFailedPayment(@RequestBody UUID paymentId) {
        log.info("Метод для эмуляции отказа в оплате платежного шлюза для заказа (ID: {})", paymentId);
        paymentService.emulateFailedPayment(paymentId);
    }
}
