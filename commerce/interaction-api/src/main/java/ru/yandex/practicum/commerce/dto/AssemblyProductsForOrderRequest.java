package ru.yandex.practicum.commerce.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class AssemblyProductsForOrderRequest {

    private Map<UUID, Long> products;
    private UUID orderId;
}