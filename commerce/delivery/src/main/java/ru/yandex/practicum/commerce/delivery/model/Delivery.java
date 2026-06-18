package ru.yandex.practicum.commerce.delivery.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.commerce.dto.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    private UUID deliveryId = UUID.randomUUID();

    private UUID orderId;

    private String fromCountry;
    private String fromCity;
    private String fromStreet;
    private String fromHouse;
    private String fromFlat;

    private String toCountry;
    private String toCity;
    private String toStreet;
    private String toHouse;
    private String toFlat;

    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState = DeliveryState.CREATED;
}