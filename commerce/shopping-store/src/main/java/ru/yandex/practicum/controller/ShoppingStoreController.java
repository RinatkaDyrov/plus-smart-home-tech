package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;
import ru.yandex.practicum.service.StoreService;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final StoreService storeService;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestBody ProductCategory category,
                                        Pageable pageable) {
        return storeService.getProductsPageable(category, pageable);
    }

    @PutMapping
    public ProductDto addProduct(@RequestBody Product product) {
        return storeService.addProduct(product);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }


}
