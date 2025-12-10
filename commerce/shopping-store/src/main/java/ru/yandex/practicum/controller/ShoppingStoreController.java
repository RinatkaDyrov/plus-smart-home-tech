package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.controller.page.PageResponse;
import ru.yandex.practicum.product.ProductCategory;
import ru.yandex.practicum.product.ProductDto;
import ru.yandex.practicum.product.QuantityState;
import ru.yandex.practicum.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.StoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {

    private final StoreService storeService;

    @GetMapping
    public PageResponse<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        Pageable pageable) {
        log.info("Запрос на продукты категории {} с настройками {}", category, pageable);
        Page<ProductDto> page = storeService.getProductsPageable(category, pageable);
        return new PageResponse<>(page);
    }

    @PutMapping
    public ProductDto addProduct(@RequestBody ProductDto dto) {
        return storeService.addProduct(dto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody @Valid ProductDto productDto) {
        return storeService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        return storeService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestParam UUID productId, @RequestParam QuantityState quantityState) {
        return storeService.setProductQuantityState(new SetProductQuantityStateRequest(productId, quantityState));
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return storeService.findProductById(productId);
    }
}