package ru.yandex.practicum.commerce.order.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.order.model.Order;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();

        dto.setOrderId(order.getOrderId());
        dto.setShoppingCartId(order.getShoppingCartId());
        dto.setProducts(order.getProducts());
        dto.setPaymentId(order.getPaymentId());
        dto.setDeliveryId(order.getDeliveryId());
        dto.setState(order.getState());
        dto.setDeliveryWeight(order.getDeliveryWeight());
        dto.setDeliveryVolume(order.getDeliveryVolume());
        dto.setFragile(order.getFragile());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDeliveryPrice(order.getDeliveryPrice());
        dto.setProductPrice(order.getProductPrice());

        return dto;
    }
}
