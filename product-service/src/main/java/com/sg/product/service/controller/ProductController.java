package com.sg.product.service.controller;

import com.sg.product.service.dto.ProductRequest;
import com.sg.product.service.dto.ProductResponse;
import com.sg.product.service.model.Product;
import com.sg.product.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService service;

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getAvailableQuantity());
    }

    @GetMapping
    public Flux<ProductResponse> list() {
        log.info("Listing products");
        return service.findAll().map(this::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> get(@PathVariable String id) {
        log.info("Get product id={}", id);
        return service.findById(id)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductResponse>> create(@RequestBody ProductRequest req) {
        log.info("Create product name={}", req.getName());
        return service.create(req)
                .map(p -> new ResponseEntity<>(toResponse(p), HttpStatus.CREATED));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductResponse>> update(@PathVariable String id, @RequestBody ProductRequest req) {
        log.info("Update product id={}", id);
        return service.update(id, req)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        log.info("Delete product id={}", id);
        return service.delete(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }
}

