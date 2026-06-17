package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

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

    @Override
    @PostMapping("/api/v1/warehouse/assembly")
    public BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest request) {
        return service.assemblyProductsForOrder(request);
    }

    @Override
    @PostMapping("/api/v1/warehouse/shipped")
    public void shippedToDelivery(@RequestBody ShippedToDeliveryRequest request) {
        service.shippedToDelivery(request);
    }

    @Override
    @PostMapping("/api/v1/warehouse/return")
    public void acceptReturn(@RequestBody Map<UUID, Long> products) {
        service.acceptReturn(products);
    }
}