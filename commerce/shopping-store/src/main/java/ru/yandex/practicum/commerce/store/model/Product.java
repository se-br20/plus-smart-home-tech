package ru.yandex.practicum.commerce.store.model;

import jakarta.persistence.*;
import ru.yandex.practicum.commerce.dto.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    private UUID productId;

    private String productName;

    private String description;

    private String imageSrc;

    @Enumerated(EnumType.STRING)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    private BigDecimal price;

    @PrePersist
    public void prePersist() {
        if (productId == null) {
            productId = UUID.randomUUID();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductState getProductState() {
        return productState;
    }

    public void setProductState(ProductState productState) {
        this.productState = productState;
    }

    public QuantityState getQuantityState() {
        return quantityState;
    }

    public void setQuantityState(QuantityState quantityState) {
        this.quantityState = quantityState;
    }
}