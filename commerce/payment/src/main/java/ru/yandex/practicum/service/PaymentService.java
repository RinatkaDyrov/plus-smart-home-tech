package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.yandex.practicum.feignClient.OrderClient;
import ru.yandex.practicum.feignClient.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.payment.PaymentDto;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Transactional
    public PaymentDto create(OrderDto orderDto) {
        Payment payment = Payment.builder()
                .id(orderDto.getPaymentId())
                .totalPrice(orderDto.getTotalPrice())
                .deliveryPrice(orderDto.getDeliveryPrice())
                .productPrice(orderDto.getProductPrice())
                .feeTotal(orderDto.getTotalPrice().multiply(BigDecimal.valueOf(0.1)))
                .status(PaymentStatus.PENDING)
                .build();
        return PaymentMapper.toDto(paymentRepository.save(payment));
    }

    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        return orderDto.getTotalPrice()
                .add(orderDto.getTotalPrice().multiply(BigDecimal.valueOf(0.1)))
                .add(orderDto.getDeliveryPrice());
    }

    @Transactional
    public void refundOrder(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Не найдены данные об оплате"));

        payment.setStatus(PaymentStatus.SUCCESS);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderClient.payment(payment.getOrderId());
            }
        });
    }

    public BigDecimal calculateOrderTotalCost(OrderDto orderDto) {
        Map<UUID, BigDecimal> prices = shoppingStoreClient.getProductPrices(
                new ArrayList<>(orderDto.getProducts().keySet())
        );

        return orderDto.getProducts().entrySet().stream()
                .map(entry -> prices.get(entry.getKey()).multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void emulateFailedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Не найдены данные об оплате"));

        payment.setStatus(PaymentStatus.FAILED);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderClient.paymentFailed(payment.getOrderId());
            }
        });
    }
}
