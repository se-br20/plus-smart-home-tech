package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@Validated
@RestController
public class WarehouseController implements WarehouseClient {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @Override
    @PutMapping("/api/v1/warehouse")
    public void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request) {
        service.newProductInWarehouse(request);
    }

    @Override
    @PostMapping("/api/v1/warehouse/check")
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody ShoppingCartDto cart) {
        return service.checkProductQuantityEnoughForShoppingCart(cart);
    }

    @Override
    @PostMapping("/api/v1/warehouse/add")
    public void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request) {
        service.addProductToWarehouse(request);
    }

    @Override
    @GetMapping("/api/v1/warehouse/address")
    public AddressDto getWarehouseAddress() {
        return service.getWarehouseAddress();
    }
}