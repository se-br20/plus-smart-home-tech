package ru.yandex.practicum.commerce.delivery.model;

import jakarta.persistence.*;
import ru.yandex.practicum.commerce.dto.DeliveryState;

import java.util.UUID;

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

    public UUID getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(UUID deliveryId) {
        this.deliveryId = deliveryId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getFromCountry() {
        return fromCountry;
    }

    public void setFromCountry(String fromCountry) {
        this.fromCountry = fromCountry;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getFromStreet() {
        return fromStreet;
    }

    public void setFromStreet(String fromStreet) {
        this.fromStreet = fromStreet;
    }

    public String getFromHouse() {
        return fromHouse;
    }

    public void setFromHouse(String fromHouse) {
        this.fromHouse = fromHouse;
    }

    public String getFromFlat() {
        return fromFlat;
    }

    public void setFromFlat(String fromFlat) {
        this.fromFlat = fromFlat;
    }

    public String getToCountry() {
        return toCountry;
    }

    public void setToCountry(String toCountry) {
        this.toCountry = toCountry;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getToStreet() {
        return toStreet;
    }

    public void setToStreet(String toStreet) {
        this.toStreet = toStreet;
    }

    public String getToHouse() {
        return toHouse;
    }

    public void setToHouse(String toHouse) {
        this.toHouse = toHouse;
    }

    public String getToFlat() {
        return toFlat;
    }

    public void setToFlat(String toFlat) {
        this.toFlat = toFlat;
    }

    public DeliveryState getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
    }
}