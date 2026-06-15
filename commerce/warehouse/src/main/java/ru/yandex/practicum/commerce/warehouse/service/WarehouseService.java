package ru.yandex.practicum.commerce.warehouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class WarehouseService {

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    private final WarehouseProductRepository repository;

    public WarehouseService(WarehouseProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (repository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Товар уже зарегистрирован на складе: " + request.getProductId()
            );
        }

        WarehouseProduct product = new WarehouseProduct();
        product.setProductId(request.getProductId());
        product.setFragile(Boolean.TRUE.equals(request.getFragile()));
        product.setWidth(request.getDimension().getWidth());
        product.setHeight(request.getDimension().getHeight());
        product.setDepth(request.getDimension().getDepth());
        product.setWeight(request.getWeight());
        product.setQuantity(0L);

        repository.save(product);
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товара нет на складе: " + request.getProductId()
                ));

        product.setQuantity(product.getQuantity() + request.getQuantity());
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (var entry : cart.getProducts().entrySet()) {
            WarehouseProduct product = repository.findById(entry.getKey())
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                            "Товара нет на складе: " + entry.getKey()
                    ));

            Long requestedQuantity = entry.getValue();

            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Недостаточно товара на складе: " + entry.getKey()
                );
            }

            totalWeight += product.getWeight() * requestedQuantity;
            totalVolume += product.getWidth()
                    * product.getHeight()
                    * product.getDepth()
                    * requestedQuantity;

            if (Boolean.TRUE.equals(product.getFragile())) {
                hasFragile = true;
            }
        }

        BookedProductsDto dto = new BookedProductsDto();
        dto.setDeliveryWeight(totalWeight);
        dto.setDeliveryVolume(totalVolume);
        dto.setFragile(hasFragile);

        return dto;
    }

    public AddressDto getWarehouseAddress() {
        AddressDto address = new AddressDto();
        address.setCountry(CURRENT_ADDRESS);
        address.setCity(CURRENT_ADDRESS);
        address.setStreet(CURRENT_ADDRESS);
        address.setHouse(CURRENT_ADDRESS);
        address.setFlat(CURRENT_ADDRESS);
        return address;
    }
}