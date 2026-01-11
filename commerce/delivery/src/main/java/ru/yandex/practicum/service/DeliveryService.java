package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.exception.delivery.NoDeliveryFoundException;
import ru.yandex.practicum.feignClient.OrderClient;
import ru.yandex.practicum.feignClient.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {
    private static final BigDecimal DEFAULT_COST = new BigDecimal("5");
    private static final BigDecimal WAREHOUSE_ADDRESS_MULTIPLE = new BigDecimal("2");
    private static final BigDecimal FRAGILE_MULTIPLE = new BigDecimal("0.2");
    private static final BigDecimal DELIVERY_WEIGHT_MULTIPLE = new BigDecimal("0.3");
    private static final BigDecimal DELIVERY_VOLUME_MULTIPLE = new BigDecimal("0.3");
    private static final BigDecimal DELIVERY_DISTANCE_MULTIPLE = new BigDecimal("0.2");

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper mapper;
    private final WarehouseClient warehouseClient;
    private final OrderClient orderClient;
    private final AddressService addressService;

    public DeliveryDto createOrder(DeliveryDto deliveryDto) {
        Delivery delivery = mapper.toDelivery(deliveryDto);

        delivery.setFromAddress(addressService.getOrCreate(delivery.getFromAddress()));
        delivery.setToAddress(addressService.getOrCreate(delivery.getToAddress()));

        if (delivery.getDeliveryState() == null) {
            delivery.setDeliveryState(DeliveryState.CREATED);
        }

        return mapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.completeOrder(delivery.getOrderId());
    }

    public void pickedDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        orderClient.assembly(delivery.getOrderId());
        warehouseClient.shippedToDelivery(new ShippedToDeliveryRequest(
                delivery.getOrderId(), delivery.getDeliveryId()));
    }

    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryOrThrow(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.assemblyFailed(delivery.getOrderId());
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = getDeliveryOrThrow(orderDto.getDeliveryId());

        BigDecimal cost = DEFAULT_COST;

        AddressDto warehouseAddress = warehouseClient.getCurrentWarehouseAddress();

        if ("ADDRESS_2".equals(warehouseAddress.getCity())) {
            cost = cost.add(cost.multiply(WAREHOUSE_ADDRESS_MULTIPLE));
        }

        boolean fragile = Boolean.TRUE.equals(delivery.getFragile());
        if (fragile) {
            cost = cost.add(cost.multiply(FRAGILE_MULTIPLE));
        }

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryWeight()).multiply(DELIVERY_WEIGHT_MULTIPLE));

        cost = cost.add(BigDecimal.valueOf(orderDto.getDeliveryVolume()).multiply(DELIVERY_VOLUME_MULTIPLE));

        boolean sameStreet = warehouseAddress.getCity().equals(delivery.getToAddress().getCity())
                && warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet());

        if (!sameStreet) {
            return cost;
        } else {
            return cost.add(cost.multiply(DELIVERY_DISTANCE_MULTIPLE));
        }
    }

    private Delivery getDeliveryOrThrow(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "Доставка с ID " + deliveryId + " не найдена",
                        "Доставка не найдена"
                ));
    }
}
