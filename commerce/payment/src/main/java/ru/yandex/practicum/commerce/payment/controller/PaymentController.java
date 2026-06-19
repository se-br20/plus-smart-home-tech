package ru.yandex.practicum.commerce.payment.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.dto.PaymentDto;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentDto payment(@RequestBody OrderDto order) {
        return service.payment(order);
    }

    @PostMapping("/productCost")
    public BigDecimal productCost(@RequestBody OrderDto order) {
        return service.productCost(order);
    }

    @PostMapping("/totalCost")
    public BigDecimal getTotalCost(@RequestBody OrderDto order) {
        return service.getTotalCost(order);
    }

    @PostMapping("/refund")
    public void paymentSuccess(@RequestBody UUID paymentId) {
        service.paymentSuccess(paymentId);
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        service.paymentFailed(paymentId);
    }
}