package ru.yandex.practicum.commerce.cart.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {

    @Id
    private UUID shoppingCartId = UUID.randomUUID();

    private String username;

    private Boolean active = true;

    @ElementCollection
    @CollectionTable(
            name = "shopping_cart_products",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products = new HashMap<>();

    public UUID getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(UUID shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Map<UUID, Long> getProducts() {
        return products;
    }

    public void setProducts(Map<UUID, Long> products) {
        this.products = products;
    }
}
