package com.sg.product.service.service;

import com.sg.product.service.dto.ProductRequest;
import com.sg.product.service.model.Product;
import com.sg.product.service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository repository;

    public Flux<Product> findAll() {
        log.debug("Fetching all products");
        return repository.findAll();
    }

    public Mono<Product> findById(String id) {
        log.debug("Fetching product by id={}", id);
        return repository.findById(id);
    }

    public Mono<Product> create(ProductRequest req) {
        log.info("Creating product name={} price={} qty={}", req.getName(), req.getPrice(), req.getAvailableQuantity());
        Product p = Product.builder()
                .name(req.getName())
                .price(req.getPrice())
                .availableQuantity(req.getAvailableQuantity())
                .build();
        return repository.save(p);
    }

    public Mono<Product> update(String id, ProductRequest req) {
        log.info("Updating product id={} name={} price={} qty={}", id, req.getName(), req.getPrice(), req.getAvailableQuantity());
        Product p = Product.builder()
                .id(id)
                .name(req.getName())
                .price(req.getPrice())
                .availableQuantity(req.getAvailableQuantity())
                .build();
        return repository.update(id, p);
    }

    public Mono<Void> delete(String id) {
        log.info("Deleting product id={}", id);
        return repository.deleteById(id);
    }
}

