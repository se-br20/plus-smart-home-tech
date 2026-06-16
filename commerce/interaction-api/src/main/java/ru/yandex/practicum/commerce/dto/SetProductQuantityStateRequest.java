package ru.yandex.practicum.commerce.dto;

import java.util.UUID;

public class SetProductQuantityStateRequest {

    private UUID productId;
    private QuantityState quantityState;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public QuantityState getQuantityState() {
        return quantityState;
    }

    public void setQuantityState(QuantityState quantityState) {
        this.quantityState = quantityState;
    }
}
