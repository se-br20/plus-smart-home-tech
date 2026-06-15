package ru.yandex.practicum.commerce.dto;

import java.util.UUID;

public class AddProductToWarehouseRequest {

    private UUID productId;
    private Long quantity;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}