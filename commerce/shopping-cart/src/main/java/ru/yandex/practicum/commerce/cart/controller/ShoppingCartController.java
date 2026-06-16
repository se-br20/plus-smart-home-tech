package ru.yandex.practicum.commerce.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.client.ShoppingCartClient;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService service;

    public ShoppingCartController(ShoppingCartService service) {
        this.service = service;
    }

    @Override
    public ShoppingCartDto getShoppingCart(@NotBlank String username) {
        return service.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(@NotBlank String username,
                                                    @NotEmpty Map<UUID, Long> products) {
        return service.addProductToShoppingCart(username, products);
    }

    @Override
    public void deactivateCurrentShoppingCart(@NotBlank String username) {
        service.deactivateCurrentShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(@NotBlank String username,
                                                  @NotEmpty List<UUID> productIds) {
        return service.removeFromShoppingCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(@NotBlank String username,
                                                 @Valid ChangeProductQuantityRequest request) {
        return service.changeProductQuantity(username, request);
    }
}