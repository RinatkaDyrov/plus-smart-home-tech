package ru.yandex.practicum.mapper;

import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.payment.PaymentDto;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDto.builder()
                .paymentId(payment.getId())
                .totalPayment(payment.getTotalPrice())
                .deliveryTotal(payment.getDeliveryPrice())
                .feeTotal(payment.getFeeTotal())
                .build();
    }
}