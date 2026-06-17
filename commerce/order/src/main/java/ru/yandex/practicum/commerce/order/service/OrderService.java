package ru.yandex.practicum.commerce.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.client.DeliveryClient;
import ru.yandex.practicum.commerce.client.PaymentClient;
import ru.yandex.practicum.commerce.client.WarehouseClient;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository repository,
                        OrderMapper mapper,
                        WarehouseClient warehouseClient,
                        DeliveryClient deliveryClient,
                        PaymentClient paymentClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.warehouseClient = warehouseClient;
        this.deliveryClient = deliveryClient;
        this.paymentClient = paymentClient;
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.getShoppingCart();

        BookedProductsDto booked = warehouseClient.checkProductQuantityEnoughForShoppingCart(cart);

        Order order = new Order();
        order.setShoppingCartId(cart.getShoppingCartId());
        order.setProducts(cart.getProducts());
        order.setState(OrderState.NEW);
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.getFragile());

        Order saved = repository.save(order);

        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();

        DeliveryDto delivery = new DeliveryDto();
        delivery.setOrderId(saved.getOrderId());
        delivery.setFromAddress(warehouseAddress);
        delivery.setToAddress(request.getDeliveryAddress());
        delivery.setDeliveryState(DeliveryState.CREATED);

        DeliveryDto plannedDelivery = deliveryClient.planDelivery(delivery);
        saved.setDeliveryId(plannedDelivery.getDeliveryId());

        return mapper.toDto(repository.save(saved));
    }

    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = getOrder(orderId);

        if (order.getState() != OrderState.ASSEMBLED) {
            AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest();
            request.setOrderId(order.getOrderId());
            request.setProducts(order.getProducts());

            BookedProductsDto booked = warehouseClient.assemblyProductsForOrder(request);

            order.setDeliveryWeight(booked.getDeliveryWeight());
            order.setDeliveryVolume(booked.getDeliveryVolume());
            order.setFragile(booked.getFragile());
            order.setState(OrderState.ASSEMBLED);
        }

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrder(orderId);

        BigDecimal deliveryCost = deliveryClient.deliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrder(orderId);

        OrderDto dto = mapper.toDto(order);

        BigDecimal productCost = paymentClient.productCost(dto);
        order.setProductPrice(productCost);

        dto.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.getTotalCost(dto);
        order.setTotalPrice(totalCost);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = getOrder(orderId);

        PaymentDto payment = paymentClient.payment(mapper.toDto(order));

        order.setPaymentId(payment.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);

        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto paymentSuccess(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.toDto(repository.save(order));
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = getOrder(request.getOrderId());

        warehouseClient.acceptReturn(request.getProducts());

        order.setState(OrderState.PRODUCT_RETURNED);
        return mapper.toDto(repository.save(order));
    }

    private Order getOrder(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Заказ не найден: " + orderId));
    }
}