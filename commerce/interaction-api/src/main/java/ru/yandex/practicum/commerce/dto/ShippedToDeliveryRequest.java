package ru.yandex.practicum.commerce.dto;

import java.util.UUID;

public class ShippedToDeliveryRequest {
    private UUID orderId;
    private UUID deliveryId;

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}