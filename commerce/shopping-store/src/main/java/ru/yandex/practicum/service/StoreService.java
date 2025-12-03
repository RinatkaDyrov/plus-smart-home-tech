package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.handler.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;
import ru.yandex.practicum.product.ProductState;
import ru.yandex.practicum.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.repository.StoreRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsPageable(ProductCategory category, Pageable pageable) {
        return storeRepository.findByProductCategory(category, pageable).map(mapper::mapToProductDto);
    }

    public ProductDto addProduct(ProductDto dto) {
        return mapper.mapToProductDto(storeRepository.save(mapper.mapToProduct(dto)));
    }

    public ProductDto updateProduct(ProductDto productDto) {
        Product product = getProductFromDB(productDto.getProductId());
        mapper.updateEntityFromDto(productDto, product);
        return mapper.mapToProductDto(product);
    }

    public boolean removeProduct(UUID productId) {
        getProductFromDB(productId).setProductState(ProductState.DEACTIVATE);
        return true;
    }

    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        getProductFromDB(request.getProductId()).setQuantityState(request.getQuantityState());
        return true;
    }

    @Transactional(readOnly = true)
    public ProductDto findProductById(UUID productId) {
        return mapper.mapToProductDto(getProductFromDB(productId));
    }

    private Product getProductFromDB(UUID id) {
        return storeRepository.findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(
                                "Продукт с ID " + id + " не найден",
                                "Продукт не найден"
                        )
                );
    }
}
