package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.feignClient.ShoppingStoreClient;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.product.QuantityState;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.*;

import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;
    private final BookingRepository bookingRepository;
    private final ShoppingStoreClient shoppingStoreClient;

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    public void addNewToWarehouse(NewProductInWarehouseRequest request) {
        repository.findById(request.getProductId())
                .ifPresent(warehouseProduct -> {
                            throw new SpecifiedProductAlreadyInWarehouseException(
                                    "Товар уже создан", "Товар с id " + request.getProductId() + " уже создан"
                            );
                        }
                );
        repository.save(mapper.mapToProduct(request));
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkProductsFromCart(ShoppingCartDto cartDto) {
        Map<UUID, Long> products = cartDto.getProducts();
        List<WarehouseProduct> warehouseProducts = repository.findAllById(products.keySet());

        if (warehouseProducts.size() != products.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Не все товары найдены на складе",
                    "Некоторые товары отсутствуют в БД склада"
            );
        }

        for (WarehouseProduct p : warehouseProducts) {
            long requiredQty = products.get(p.getProductId());
            if (p.getQuantity() < requiredQty) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара",
                        "Товара с id " + p.getProductId() + " доступно " + p.getQuantity()
                );
            }
        }

        return getBookedProducts(warehouseProducts, products);
    }

    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товар не найден",
                        "Товар с Id" + request.getProductId() + " не найден")
                );
        product.setQuantity(request.getQuantity());
    }

    @Transactional(readOnly = true)
    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    public void shippedToDelivery(ShippedToDeliveryRequest deliveryRequest) {
        Booking booking = bookingRepository.findByOrderId(deliveryRequest.getOrderId()).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе.", "Ошибка"));
        booking.setDeliveryId(deliveryRequest.getDeliveryId());
    }

    public void returnProducts(Map<UUID, Long> productsToReturn) {
        List<WarehouseProduct> products = repository.findAllById(productsToReturn.keySet());
        for (WarehouseProduct p : products) {
            Long addQty = productsToReturn.get(p.getProductId());
            if (addQty != null && addQty > 0) {
                p.setQuantity(p.getQuantity() + addQty);
                updateProductQuantityInShoppingStore(p);
            }
        }
    }

    public BookedProductsDto assembly(AssemblyProductsForOrderRequest request) {
        ShoppingCartDto cartDto = new ShoppingCartDto();
        cartDto.setProducts(request.getProducts());
        cartDto.setShoppingCartId(request.getOrderId());

        BookedProductsDto booked = checkProductsFromCart(cartDto);


        for (Map.Entry<UUID, Long> entry : request.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                            "Товар не найден",
                            "Товар с id " + entry.getKey() + " отсутствует"
                    ));
            product.setQuantity(product.getQuantity() - entry.getValue());
            updateProductQuantityInShoppingStore(product);
        }


        Booking newBooking = Booking.builder()
                .shoppingCartId(request.getOrderId())
                .products(request.getProducts())
                .deliveryWeight(booked.getDeliveryWeight())
                .deliveryVolume(booked.getDeliveryVolume())
                .fragile(booked.getFragile())
                .build();
        bookingRepository.save(newBooking);

        return booked;
    }

    private BookedProductsDto getBookedProducts(Collection<WarehouseProduct> products, Map<UUID, Long> quantities) {
        double totalWeight = products.stream()
                .mapToDouble(p -> p.getWeight() * quantities.get(p.getProductId()))
                .sum();
        double totalVolume = products.stream()
                .mapToDouble(p -> p.getWidth() * p.getHeight() * p.getDepth() * quantities.get(p.getProductId()))
                .sum();
        boolean hasFragile = products.stream().anyMatch(WarehouseProduct::getFragile);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragile)
                .build();
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        UUID productId = product.getProductId();
        QuantityState quantityState;
        Long qty = product.getQuantity();

        if (qty == 0) {
            quantityState = QuantityState.ENDED;
        } else if
        (qty < 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (qty < 100) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.MANY;
        }

        shoppingStoreClient.changeQuantityState(productId, quantityState);
    }
}
