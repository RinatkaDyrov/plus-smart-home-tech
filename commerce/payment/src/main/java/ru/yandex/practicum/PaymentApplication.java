package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.feignClient.DeliveryClient;
import ru.yandex.practicum.feignClient.PaymentClient;
import ru.yandex.practicum.feignClient.ShoppingCartClient;
import ru.yandex.practicum.feignClient.WarehouseClient;

@SpringBootApplication
@EnableFeignClients(clients = {ShoppingCartClient.class, WarehouseClient.class, DeliveryClient.class, PaymentClient.class})
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}