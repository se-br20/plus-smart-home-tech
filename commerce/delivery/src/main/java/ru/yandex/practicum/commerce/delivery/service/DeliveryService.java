package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.OrderClient;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.delivery.config.DeliveryProperties;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.dto.DeliveryDto;
import ru.yandex.practicum.commerce.dto.DeliveryState;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.commerce.exception.NoDeliveryFoundException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryProperties properties;

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
            throw new NoDeliveryFoundException("В заказе нет deliveryId");
        }

        Delivery delivery = repository.findById(order.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException(
                        "Доставка не найдена: " + order.getDeliveryId()
                ));

        BigDecimal result = properties.getBaseCost();

        if (containsAddress2(delivery)) {
            result = result.add(properties.getBaseCost().multiply(properties.getAddress2Multiplier()));
        } else {
            result = result.add(properties.getBaseCost().multiply(properties.getAddress1Multiplier()));
        }

        if (Boolean.TRUE.equals(order.getFragile())) {
            result = result.add(result.multiply(properties.getFragileRate()));
        }

        if (order.getDeliveryWeight() != null) {
            result = result.add(BigDecimal.valueOf(order.getDeliveryWeight()).multiply(properties.getWeightRate()));
        }

        if (order.getDeliveryVolume() != null) {
            result = result.add(BigDecimal.valueOf(order.getDeliveryVolume()).multiply(properties.getVolumeRate()));
        }

        if (!Objects.equals(delivery.getFromStreet(), delivery.getToStreet())) {
            result = result.add(result.multiply(properties.getDifferentStreetRate()));
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
        orderClient.delivery(orderId);
    }

    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = getByOrderId(orderId);

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        repository.save(delivery);

        orderClient.deliverySuccess(orderId);
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
        return contains(delivery.getFromCountry(), properties.getAddress2Marker())
                || contains(delivery.getFromCity(), properties.getAddress2Marker())
                || contains(delivery.getFromStreet(), properties.getAddress2Marker())
                || contains(delivery.getFromHouse(), properties.getAddress2Marker())
                || contains(delivery.getFromFlat(), properties.getAddress2Marker());
    }

    private boolean contains(String value, String marker) {
        return value != null && marker != null && value.contains(marker);
    }
}