package ru.yandex.practicum.commerce.delivery.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.DeliveryDto;

@Component
public class DeliveryMapper {

    public Delivery toEntity(DeliveryDto dto) {
        Delivery delivery = new Delivery();

        delivery.setDeliveryId(dto.getDeliveryId());
        delivery.setOrderId(dto.getOrderId());
        delivery.setDeliveryState(dto.getDeliveryState());

        if (dto.getFromAddress() != null) {
            delivery.setFromCountry(dto.getFromAddress().getCountry());
            delivery.setFromCity(dto.getFromAddress().getCity());
            delivery.setFromStreet(dto.getFromAddress().getStreet());
            delivery.setFromHouse(dto.getFromAddress().getHouse());
            delivery.setFromFlat(dto.getFromAddress().getFlat());
        }

        if (dto.getToAddress() != null) {
            delivery.setToCountry(dto.getToAddress().getCountry());
            delivery.setToCity(dto.getToAddress().getCity());
            delivery.setToStreet(dto.getToAddress().getStreet());
            delivery.setToHouse(dto.getToAddress().getHouse());
            delivery.setToFlat(dto.getToAddress().getFlat());
        }

        return delivery;
    }

    public DeliveryDto toDto(Delivery delivery) {
        DeliveryDto dto = new DeliveryDto();

        dto.setDeliveryId(delivery.getDeliveryId());
        dto.setOrderId(delivery.getOrderId());
        dto.setDeliveryState(delivery.getDeliveryState());

        AddressDto from = new AddressDto();
        from.setCountry(delivery.getFromCountry());
        from.setCity(delivery.getFromCity());
        from.setStreet(delivery.getFromStreet());
        from.setHouse(delivery.getFromHouse());
        from.setFlat(delivery.getFromFlat());
        dto.setFromAddress(from);

        AddressDto to = new AddressDto();
        to.setCountry(delivery.getToCountry());
        to.setCity(delivery.getToCity());
        to.setStreet(delivery.getToStreet());
        to.setHouse(delivery.getToHouse());
        to.setFlat(delivery.getToFlat());
        dto.setToAddress(to);

        return dto;
    }
}