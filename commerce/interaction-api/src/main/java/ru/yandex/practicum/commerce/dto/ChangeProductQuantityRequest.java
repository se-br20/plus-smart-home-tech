package ru.yandex.practicum.commerce.dto;

import java.util.UUID;

public class ChangeProductQuantityRequest {

    private UUID productId;
    private Long newQuantity;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Long getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Long newQuantity) {
        this.newQuantity = newQuantity;
    }
}
