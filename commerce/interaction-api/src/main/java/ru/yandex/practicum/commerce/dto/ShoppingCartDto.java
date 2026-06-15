package ru.yandex.practicum.commerce.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShoppingCartDto {

    private UUID shoppingCartId;
    private Map<UUID, Long> products = new HashMap<>();

    public UUID getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(UUID shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public Map<UUID, Long> getProducts() {
        return products;
    }

    public void setProducts(Map<UUID, Long> products) {
        this.products = products;
    }
}