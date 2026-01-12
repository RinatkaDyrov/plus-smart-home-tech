package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.delivery.DeliveryDto;
import ru.yandex.practicum.exception.common.NotAuthorizedUserException;
import ru.yandex.practicum.exception.order.NoOrderFoundException;
import ru.yandex.practicum.feignClient.DeliveryClient;
import ru.yandex.practicum.feignClient.PaymentClient;
import ru.yandex.practicum.feignClient.ShoppingCartClient;
import ru.yandex.practicum.feignClient.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.CreateNewOrderRequest;
import ru.yandex.practicum.order.OrderDto;
import ru.yandex.practicum.order.OrderState;
import ru.yandex.practicum.order.ProductReturnRequest;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.BookedProductsDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartClient shoppingCartClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(String username, Integer page, Integer size) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Необходимо указать имя пользователя", "Пользователь не авторизован");
        }
        ShoppingCartDto shoppingCartDto = shoppingCartClient.getCart(username);
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);

        List<Order> orders = orderRepository.findByShoppingCartId(shoppingCartDto.getShoppingCartId(), pageRequest);
        return orders.stream().map(orderMapper::mapToOrderDto).toList();
    }

    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order newOrder = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        BookedProductsDto bookedProducts = warehouseClient.assemblyProductsForOrder(
                new AssemblyProductsForOrderRequest(
                        request.getShoppingCart().getProducts(),
                        savedOrder.getOrderId()
                )
        );

        savedOrder.setFragile(bookedProducts.getFragile());
        savedOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        savedOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());

        savedOrder.setProductPrice(paymentClient.calculateOrderTotalCost(orderMapper.mapToOrderDto(savedOrder)));

        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(savedOrder.getOrderId())
                .fromAddress(warehouseClient.getCurrentWarehouseAddress())
                .toAddress(request.getDeliveryAddress())
                .build();

        savedOrder.setDeliveryId(deliveryClient.createOrder(deliveryDto).getDeliveryId());
        paymentClient.createPayment(orderMapper.mapToOrderDto(savedOrder));

        return orderMapper.mapToOrderDto(savedOrder);
    }

    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException(
                        "Заказ с ID " + request.getOrderId() + " не найден", "Заказ не найден")
                );

        warehouseClient.returnedProduct(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto payment(boolean success, UUID orderId) {
        Order order = findOrder(orderId);
        if (success) {
            order.setState(OrderState.PAID);
        } else {
            order.setState(OrderState.PAYMENT_FAILED);
        }
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto successfulDelivery(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.mapToOrderDto(order);
    }

    public OrderDto failedDelivery(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto completeOrder(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto calculateTotalPrice(UUID orderId) {
        Order order = findOrder(orderId);
        order.setTotalPrice(paymentClient.calculateTotalCost(orderMapper.mapToOrderDto(order)));
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto calculateDeliveryPrice(UUID orderId) {
        Order order = findOrder(orderId);
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderMapper.mapToOrderDto(order)));
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto assembly(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.mapToOrderDto(orderRepository.save(order));
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(
                        "Заказ с ID " + orderId + " не найден", "Заказ не найден")
                );
    }
}


