package ru.yandex.practicum.commerce.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.OrderClient;
import ru.yandex.practicum.commerce.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.10);

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    public PaymentService(PaymentRepository repository,
                          PaymentMapper mapper,
                          ShoppingStoreClient shoppingStoreClient,
                          OrderClient orderClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.shoppingStoreClient = shoppingStoreClient;
        this.orderClient = orderClient;
    }

    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        validateOrderProducts(order);

        BigDecimal result = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : order.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            ProductDto product = shoppingStoreClient.getProduct(productId);

            if (product.getPrice() == null) {
                throw new NotEnoughInfoInOrderToCalculateException(
                        "У товара нет цены: " + productId
                );
            }

            result = result.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto order) {
        if (order == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Заказ не передан");
        }

        BigDecimal products = order.getProductPrice();

        if (products == null) {
            products = productCost(order);
        }

        BigDecimal delivery = order.getDeliveryPrice();

        if (delivery == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не рассчитана стоимость доставки");
        }

        BigDecimal tax = products.multiply(TAX_RATE);

        return products.add(tax).add(delivery);
    }

    @Transactional
    public PaymentDto payment(OrderDto order) {
        if (order == null || order.getOrderId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет orderId");
        }

        BigDecimal products = order.getProductPrice();

        if (products == null) {
            products = productCost(order);
        }

        BigDecimal delivery = order.getDeliveryPrice();

        if (delivery == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не рассчитана стоимость доставки");
        }

        BigDecimal tax = products.multiply(TAX_RATE);
        BigDecimal total = products.add(tax).add(delivery);

        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setProductTotal(products);
        payment.setDeliveryTotal(delivery);
        payment.setFeeTotal(tax);
        payment.setTotalPayment(total);
        payment.setState(PaymentState.PENDING);

        return mapper.toDto(repository.save(payment));
    }

    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = getPayment(paymentId);

        payment.setState(PaymentState.SUCCESS);
        repository.save(payment);

        orderClient.paymentSuccess(payment.getOrderId());
    }

    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = getPayment(paymentId);

        payment.setState(PaymentState.FAILED);
        repository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
    }

    private Payment getPayment(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new NoOrderFoundException("Оплата не найдена: " + paymentId));
    }

    private void validateOrderProducts(OrderDto order) {
        if (order == null || order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе нет товаров");
        }
    }
}