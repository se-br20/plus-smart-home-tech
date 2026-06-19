package ru.yandex.practicum.commerce.delivery.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "delivery.cost")
public class DeliveryProperties {

    private BigDecimal baseCost = new BigDecimal("5.0");
    private BigDecimal address1Multiplier = new BigDecimal("1");
    private BigDecimal address2Multiplier = new BigDecimal("2");
    private BigDecimal fragileRate = new BigDecimal("0.2");
    private BigDecimal weightRate = new BigDecimal("0.3");
    private BigDecimal volumeRate = new BigDecimal("0.2");
    private BigDecimal differentStreetRate = new BigDecimal("0.2");

    private String address1Marker = "ADDRESS_1";
    private String address2Marker = "ADDRESS_2";
}