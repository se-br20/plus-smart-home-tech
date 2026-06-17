package ru.yandex.practicum.commerce.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.OrderClient;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
public class DeliveryService {

    private static final BigDecimal BASE_COST = new BigDecimal("5.0");
    private static final BigDecimal ADDRESS_1_MULTIPLIER = new BigDecimal("1");
    private static final BigDecimal ADDRESS_2_MULTIPLIER = new BigDecimal("2");
    private static final BigDecimal FRAGILE_RATE = new BigDecimal("0.2");
    private static final BigDecimal WEIGHT_RATE = new BigDecimal("0.3");
    private static final BigDecimal VOLUME_RATE = new BigDecimal("0.2");
    private static final BigDecimal DIFFERENT_STREET_RATE = new BigDecimal("0.2");

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    public DeliveryService(DeliveryRepository repository,
                           DeliveryMapper mapper,
                           OrderClient orderClient,
                           WarehouseClient warehouseClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.orderClient = orderClient;
        this.warehouseClient = warehouseClient;
    }

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = mapper.toEntity(deliveryDto);

        if (delivery.getDeliveryState() == null) {
            delivery.setDeliveryState(DeliveryState.CREATED);
        }

        Delivery saved = repository.save(delivery);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        if (order == null || order.getDeliveryId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет deliveryId");
        }

        Delivery delivery = repository.findById(order.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "Доставка не найдена: " + order.getDeliveryId()
                ));

        BigDecimal result = BASE_COST;

        if (containsAddress2(delivery)) {
            result = result.add(BASE_COST.multiply(ADDRESS_2_MULTIPLIER));
        } else {
            result = result.add(BASE_COST.multiply(ADDRESS_1_MULTIPLIER));
        }

        if (Boolean.TRUE.equals(order.getFragile())) {
            result = result.add(result.multiply(FRAGILE_RATE));
        }

        if (order.getDeliveryWeight() != null) {
            result = result.add(BigDecimal.valueOf(order.getDeliveryWeight()).multiply(WEIGHT_RATE));
        }

        if (order.getDeliveryVolume() != null) {
            result = result.add(BigDecimal.valueOf(order.getDeliveryVolume()).multiply(VOLUME_RATE));
        }

        if (!Objects.equals(delivery.getFromStreet(), delivery.getToStreet())) {
            result = result.add(result.multiply(DIFFERENT_STREET_RATE));
        }

        return result;
    }

    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        repository.save(delivery);

        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest();
        request.setOrderId(orderId);
        request.setDeliveryId(delivery.getDeliveryId());

        warehouseClient.shippedToDelivery(request);

        orderClient.assembly(orderId);
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);

        orderClient.delivery(orderId);
    }

    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.FAILED);
        repository.save(delivery);

        orderClient.deliveryFailed(orderId);
    }

    private Delivery getByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "Доставка для заказа не найдена: " + orderId
                ));
    }

    private boolean containsAddress2(Delivery delivery) {
        return contains(delivery.getFromCountry(), "ADDRESS_2")
                || contains(delivery.getFromCity(), "ADDRESS_2")
                || contains(delivery.getFromStreet(), "ADDRESS_2")
                || contains(delivery.getFromHouse(), "ADDRESS_2")
                || contains(delivery.getFromFlat(), "ADDRESS_2");
    }

    private boolean contains(String value, String text) {
        return value != null && value.contains(text);
    }
}