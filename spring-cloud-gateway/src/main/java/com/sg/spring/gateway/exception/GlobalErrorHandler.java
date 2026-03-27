package com.sg.spring.gateway.exception;

import com.sg.spring.gateway.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiError>> handleBindException(WebExchangeBindException ex, ServerWebExchange exchange) {
        List<String> details = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError err = new ApiError(null, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation failed", exchange.getRequest().getPath().toString(), details);
        log.debug("Validation errors: {}", details);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ApiError>> handleConstraintViolation(ConstraintViolationException ex, ServerWebExchange exchange) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + " : " + cv.getMessage())
                .collect(Collectors.toList());
        ApiError err = new ApiError(null, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Constraint violations", exchange.getRequest().getPath().toString(), details);
        log.debug("Constraint violations: {}", details);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiError>> handleBadInput(ServerWebInputException ex, ServerWebExchange exchange) {
        ApiError err = new ApiError(null, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getReason(), exchange.getRequest().getPath().toString(), List.of(ex.getMessage()));
        log.debug("Bad input: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiError>> handleGeneric(Exception ex, ServerWebExchange exchange) {
        log.error("Unhandled error in gateway", ex);
        ApiError err = new ApiError(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error", exchange.getRequest().getPath().toString(), List.of(ex.getMessage()));
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err));
    }
}

