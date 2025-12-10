package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.warehouse.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.warehouse.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.AddressDto;
import ru.yandex.practicum.warehouse.BookedProductsDto;
import ru.yandex.practicum.warehouse.NewProductInWarehouseRequest;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseProductRepository repository;
    private final WarehouseMapper mapper;

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

    public BookedProductsDto checkProductsFromCart(ShoppingCartDto cartDto) {
        Set<UUID> productIds = cartDto.getProducts().keySet();
        List<WarehouseProduct> products = repository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Не все товары найдены на складе",
                    "Некоторые товары отсутствуют в БД склада"
            );
        }

        double totalWeight = 0;
        double totalVolume = 0;
        boolean isFragile = false;

        for (WarehouseProduct product : products) {
            Long productsQuantityInCart = cartDto.getProducts().get(product.getProductId());

            if (product.getQuantity() < productsQuantityInCart) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара",
                        "Товара с id " + product.getProductId() + " доступно " + product.getQuantity()
                );
            }

            totalWeight += product.getWeight() * productsQuantityInCart;
            totalVolume += product.getWeight() * product.getHeight() * product.getDepth() * productsQuantityInCart;

            if (product.getFragile()) {
                isFragile = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(isFragile)
                .build();
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
}
