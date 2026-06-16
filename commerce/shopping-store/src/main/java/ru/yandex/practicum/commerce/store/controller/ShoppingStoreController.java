package ru.yandex.practicum.commerce.store.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.store.service.ShoppingStoreService;
import ru.yandex.practicum.commerce.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@RestController
public class ShoppingStoreController implements ShoppingStoreClient {

    private final ShoppingStoreService service;

    public ShoppingStoreController(ShoppingStoreService service) {
        this.service = service;
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory category,
                                        Integer page,
                                        Integer size,
                                        List<String> sort) {
        return service.getProducts(category, page, size, sort);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        return service.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        return service.updateProduct(productDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        return service.getProduct(productId);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        return service.removeProductFromStore(productId);
    }

    @Override
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return service.setProductQuantityState(request);
    }
}
