package com.sg.product.service.repository;

import com.sg.product.service.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> store = new ConcurrentHashMap<>();
    private final AtomicInteger idSeq = new AtomicInteger(1);

    @PostConstruct
    public void init() {
        // seed 20 dummy products
        IntStream.rangeClosed(1, 20).forEach(i -> {
            String id = "prod-" + i;
            Product p = Product.builder()
                    .id(id)
                    .name("Product " + i)
                    .price(BigDecimal.valueOf(10 + i))
                    .availableQuantity(10 + i)
                    .build();
            store.put(id, p);
            idSeq.set(i + 1);
        });
        log.info("Seeded {} products", store.size());
    }

    @Override
    public Flux<Product> findAll() {
        log.debug("Repository findAll");
        return Flux.fromIterable(store.values());
    }

    @Override
    public Mono<Product> findById(String id) {
        log.debug("Repository findById: {}", id);
        return Mono.justOrEmpty(store.get(id));
    }

    @Override
    public Mono<Product> save(Product product) {
        if (product.getId() == null) {
            product.setId("prod-" + idSeq.getAndIncrement());
        }
        log.debug("Repository save id={}", product.getId());
        store.put(product.getId(), product);
        return Mono.just(product);
    }

    @Override
    public Mono<Product> update(String id, Product product) {
        log.debug("Repository update id={}", id);
        if (!store.containsKey(id)) return Mono.empty();
        product.setId(id);
        store.put(id, product);
        return Mono.just(product);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        log.debug("Repository delete id={}", id);
        store.remove(id);
        return Mono.empty();
    }
}
