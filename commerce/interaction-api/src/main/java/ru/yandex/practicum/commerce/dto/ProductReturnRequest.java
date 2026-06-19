package ru.yandex.practicum.commerce.dto;

import java.util.Map;
import java.util.UUID;

public class ProductReturnRequest {
    private UUID orderId;
    private Map<UUID, Long> products;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Map<UUID, Long> getProducts() {
        return products;
    }

    public void setProducts(Map<UUID, Long> products) {
        this.products = products;
    }
}