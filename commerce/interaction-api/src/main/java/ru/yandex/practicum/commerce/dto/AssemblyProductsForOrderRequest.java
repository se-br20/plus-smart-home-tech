package ru.yandex.practicum.commerce.dto;

import java.util.Map;
import java.util.UUID;

public class AssemblyProductsForOrderRequest {
    private Map<UUID, Long> products;
    private UUID orderId;

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
