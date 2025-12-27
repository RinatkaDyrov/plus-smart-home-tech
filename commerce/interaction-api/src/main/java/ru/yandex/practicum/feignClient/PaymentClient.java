package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "PAYMENT")
public interface PaymentClient {
}
