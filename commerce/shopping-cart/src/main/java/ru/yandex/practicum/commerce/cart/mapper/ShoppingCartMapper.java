package ru.yandex.practicum.commerce.cart.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCart cart) {
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(cart.getShoppingCartId());
        dto.setProducts(cart.getProducts());
        return dto;
    }
}
