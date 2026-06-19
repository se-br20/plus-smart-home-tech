package ru.yandex.practicum.commerce.payment.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.PaymentDto;
import ru.yandex.practicum.commerce.payment.model.Payment;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();

        dto.setPaymentId(payment.getPaymentId());
        dto.setTotalPayment(payment.getTotalPayment());
        dto.setDeliveryTotal(payment.getDeliveryTotal());
        dto.setFeeTotal(payment.getFeeTotal());

        return dto;
    }
}