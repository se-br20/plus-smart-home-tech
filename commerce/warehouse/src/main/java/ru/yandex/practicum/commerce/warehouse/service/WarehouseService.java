package ru.yandex.practicum.commerce.warehouse.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.commerce.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.commerce.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.commerce.warehouse.mapper.WarehouseProductMapper;
import ru.yandex.practicum.commerce.warehouse.model.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;
import ru.yandex.practicum.commerce.warehouse.repository.OrderBookingRepository;
import ru.yandex.practicum.commerce.warehouse.repository.WarehouseProductRepository;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    private final WarehouseProductRepository repository;
    private final WarehouseProductMapper mapper;
    private final OrderBookingRepository bookingRepository;

    public WarehouseService(WarehouseProductRepository repository,
                            WarehouseProductMapper mapper,
                            OrderBookingRepository bookingRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (repository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Товар уже есть на складе: " + request.getProductId()
            );
        }

        WarehouseProduct product = mapper.toEntity(request);
        repository.save(product);
    }

    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "Товара нет на складе: " + request.getProductId()
                ));

        product.setQuantity(product.getQuantity() + request.getQuantity());
        repository.save(product);
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cart) {
        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException("Корзина пуста");
        }

        Set<UUID> productIds = cart.getProducts().keySet();

        Map<UUID, WarehouseProduct> productsById = repository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : cart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            WarehouseProduct product = productsById.get(productId);

            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException(
                        "Товара нет на складе: " + productId
                );
            }

            if (product.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(
                        "Недостаточно товара на складе: " + productId
                );
            }

            totalWeight += product.getWeight() * requestedQuantity;
            totalVolume += product.getWidth() * product.getHeight() * product.getDepth() * requestedQuantity;

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

    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        if (bookingRepository.existsById(request.getOrderId())) {
            ShoppingCartDto cart = new ShoppingCartDto();
            cart.setShoppingCartId(request.getOrderId());
            cart.setProducts(request.getProducts());

            return calculateBookedProductsWithoutQuantityCheck(cart);
        }

        ShoppingCartDto cart = new ShoppingCartDto();
        cart.setShoppingCartId(request.getOrderId());
        cart.setProducts(request.getProducts());

        BookedProductsDto booked = checkProductQuantityEnoughForShoppingCart(cart);

        Map<UUID, WarehouseProduct> productsById = repository.findAllById(request.getProducts().keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        request.getProducts().forEach((productId, quantity) -> {
            WarehouseProduct product = productsById.get(productId);
            product.setQuantity(product.getQuantity() - quantity);
        });

        repository.saveAll(productsById.values());

        bookingRepository.save(new OrderBooking(request.getOrderId()));

        return booked;
    }

    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking booking = bookingRepository.findById(request.getOrderId())
                .orElseGet(() -> new OrderBooking(request.getOrderId()));

        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);
    }

    @Transactional
    public void acceptReturn(Map<UUID, Long> products) {
        Map<UUID, WarehouseProduct> productsById = repository.findAllById(products.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        products.forEach((productId, quantity) -> {
            WarehouseProduct product = productsById.get(productId);

            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException(
                        "Товара нет на складе: " + productId
                );
            }

            product.setQuantity(product.getQuantity() + quantity);
        });

        repository.saveAll(productsById.values());
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

    private BookedProductsDto calculateBookedProductsWithoutQuantityCheck(ShoppingCartDto cart) {
        Set<UUID> productIds = cart.getProducts().keySet();

        Map<UUID, WarehouseProduct> productsById = repository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : cart.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long requestedQuantity = entry.getValue();

            WarehouseProduct product = productsById.get(productId);

            if (product == null) {
                throw new NoSpecifiedProductInWarehouseException(
                        "Товара нет на складе: " + productId
                );
            }

            totalWeight += product.getWeight() * requestedQuantity;
            totalVolume += product.getWidth() * product.getHeight() * product.getDepth() * requestedQuantity;

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
}