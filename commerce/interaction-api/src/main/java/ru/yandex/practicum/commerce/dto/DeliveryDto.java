package ru.yandex.practicum.commerce.dto;

import java.util.UUID;

public class DeliveryDto {
    private UUID deliveryId;
    private AddressDto fromAddress;
    private AddressDto toAddress;
    private UUID orderId;
    private DeliveryState deliveryState;

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public DeliveryState getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
    }

    public AddressDto getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(AddressDto fromAddress) {
        this.fromAddress = fromAddress;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public AddressDto getToAddress() {
        return toAddress;
    }

    public void setToAddress(AddressDto toAddress) {
        this.toAddress = toAddress;
    }
}
