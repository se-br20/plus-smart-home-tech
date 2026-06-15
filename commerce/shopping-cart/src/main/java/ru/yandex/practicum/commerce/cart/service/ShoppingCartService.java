package ru.yandex.practicum.commerce.cart.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.exception.NoProductsInShoppingCartException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient warehouseClient;

    public ShoppingCartService(ShoppingCartRepository repository,
                               ShoppingCartMapper mapper,
                               WarehouseClient warehouseClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.warehouseClient = warehouseClient;
    }

    @Transactional
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart cart = getOrCreateActiveCart(username);
        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        ShoppingCart cart = getOrCreateActiveCart(username);

        products.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Long::sum)
        );

        warehouseClient.checkProductQuantityEnoughForShoppingCart(mapper.toDto(cart));

        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        ShoppingCart cart = getActiveCart(username);

        if (cart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("Корзина пуста");
        }

        productIds.forEach(cart.getProducts()::remove);

        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getActiveCart(username);

        if (cart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("Корзина пуста");
        }

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());

        warehouseClient.checkProductQuantityEnoughForShoppingCart(mapper.toDto(cart));

        return mapper.toDto(cart);
    }

    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        ShoppingCart cart = getActiveCart(username);
        cart.setActive(false);
        repository.save(cart);
    }

    private ShoppingCart getOrCreateActiveCart(String username) {
        return repository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    return repository.save(newCart);
                });
    }

    private ShoppingCart getActiveCart(String username) {
        return repository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Активная корзина не найдена"));
    }
}