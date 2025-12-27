package ru.yandex.practicum.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "DELIVERY")
public interface DeliveryClient {
}
