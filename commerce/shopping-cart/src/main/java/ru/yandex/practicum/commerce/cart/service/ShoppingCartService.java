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
import ru.yandex.practicum.commerce.exception.NotAuthorizedUserException;

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
        checkUsername(username);

        ShoppingCart cart = getOrCreateActiveCart(username);

        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> products) {
        checkUsername(username);

        ShoppingCart cart = getOrCreateActiveCart(username);

        products.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Long::sum)
        );

        ShoppingCartDto dto = mapper.toDto(cart);

        warehouseClient.checkProductQuantityEnoughForShoppingCart(dto);

        return dto;
    }

    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        checkUsername(username);

        ShoppingCart cart = getActiveCartOrThrow(username);

        for (UUID productId : productIds) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new NoProductsInShoppingCartException(
                        "Товара нет в корзине: " + productId
                );
            }

            cart.getProducts().remove(productId);
        }

        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checkUsername(username);

        ShoppingCart cart = getActiveCartOrThrow(username);

        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException(
                    "Товара нет в корзине: " + request.getProductId()
            );
        }

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());

        ShoppingCartDto dto = mapper.toDto(cart);

        warehouseClient.checkProductQuantityEnoughForShoppingCart(dto);

        return dto;
    }

    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        checkUsername(username);

        ShoppingCart cart = getActiveCartOrThrow(username);

        cart.setActive(false);
    }

    private ShoppingCart getOrCreateActiveCart(String username) {
        return repository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUsername(username);
                    return repository.save(newCart);
                });
    }

    private ShoppingCart getActiveCartOrThrow(String username) {
        return repository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException(
                        "Активная корзина не найдена"
                ));
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }
}