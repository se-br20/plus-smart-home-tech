package ru.yandex.practicum.commerce.store.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ShoppingStoreService(ProductRepository productRepository,
                                ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category,
                                        Integer page,
                                        Integer size,
                                        List<String> sort) {
        Pageable pageable = PageRequest.of(page, size, buildSort(sort));

        return productRepository
                .findAllByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable)
                .map(productMapper::toDto);
    }

    @Transactional
    public ProductDto createNewProduct(ProductDto dto) {
        if (dto.getProductState() == null) {
            dto.setProductState(ProductState.ACTIVE);
        }

        Product saved = productRepository.save(productMapper.toEntity(dto));
        return productMapper.toDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар не найден: " + dto.getProductId()
                ));

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setImageSrc(dto.getImageSrc());
        product.setQuantityState(dto.getQuantityState());
        product.setProductState(dto.getProductState());
        product.setProductCategory(dto.getProductCategory());
        product.setPrice(dto.getPrice());

        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар не найден: " + productId
                ));
    }

    @Transactional
    public Boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар не найден: " + productId
                ));

        product.setProductState(ProductState.DEACTIVATE);
        return true;
    }

    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Товар не найден: " + request.getProductId()
                ));

        product.setQuantityState(request.getQuantityState());
        return true;
    }

    private Sort buildSort(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted();
        }

        return Sort.by(sort.stream()
                .map(this::toOrder)
                .toList());
    }

    private Sort.Order toOrder(String sortParam) {
        String[] parts = sortParam.split(",");

        String property = parts[0];

        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) {
            return Sort.Order.desc(property);
        }

        return Sort.Order.asc(property);
    }
}