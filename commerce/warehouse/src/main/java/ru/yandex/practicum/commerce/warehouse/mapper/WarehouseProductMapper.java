package ru.yandex.practicum.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.commerce.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.model.WarehouseProduct;

@Component
public class WarehouseProductMapper {

    public WarehouseProduct toEntity(NewProductInWarehouseRequest request) {
        WarehouseProduct product = new WarehouseProduct();

        product.setProductId(request.getProductId());
        product.setFragile(Boolean.TRUE.equals(request.getFragile()));
        product.setWidth(request.getDimension().getWidth());
        product.setHeight(request.getDimension().getHeight());
        product.setDepth(request.getDimension().getDepth());
        product.setWeight(request.getWeight());
        product.setQuantity(0L);

        return product;
    }
}
