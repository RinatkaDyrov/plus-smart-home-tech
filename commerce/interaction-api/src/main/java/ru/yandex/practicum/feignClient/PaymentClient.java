package ru.yandex.practicum.feignClient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "PAYMENT")
public interface PaymentClient {
    @PostMapping("/api/v1/payment")
    PaymentDto createPayment(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/totalCost")
    BigDecimal calculateTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/api/v1/payment/refund")
    void refundOrder(@RequestBody UUID paymentId);

    @PostMapping("/api/v1/payment/productCost")
    BigDecimal calculateOrderTotalCost(@RequestBody @Valid OrderDto orderDto);

    @PostMapping("/api/v1/payment/failed")
    void emulateFailedPayment(@RequestBody UUID paymentId);
}
