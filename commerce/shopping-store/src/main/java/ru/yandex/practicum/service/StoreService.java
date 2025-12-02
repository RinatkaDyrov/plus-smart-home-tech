package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;

@Service
public class StoreService {
    public Page<ProductDto> getProductsPageable(ProductCategory category, Pageable pageable) {
    }

    public ProductDto addProduct(Product product) {
    }

    public ProductDto updateProduct(ProductDto productDto) {
    }
}
