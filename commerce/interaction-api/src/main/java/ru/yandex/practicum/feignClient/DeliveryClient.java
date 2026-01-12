package ru.yandex.practicum.feignClient;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "DELIVERY")
public interface DeliveryClient {

    @PutMapping("/api/v1/delivery")
    DeliveryDto createOrder(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/api/v1/delivery/successful")
    void successfulDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/picked")
    void pickedDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/failed")
    void failedDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto);
}
