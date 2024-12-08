package com.example.approvalservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface UserClient {
    Logger log = LoggerFactory.getLogger(UserClient.class);

    // Annotated method to check inventory
    @GetExchange("/api/user")
    @CircuitBreaker(name = "user", fallbackMethod = "fallbackMethod")
    @Retry(name = "user")
    boolean exists(@RequestParam String skuCode, @RequestParam Integer quantity);

    // Fallback method for circuit breaker
    default boolean fallbackMethod(String skuCode, Integer quantity, Throwable throwable) {
        log.warn("Cannot get user with code {}, failure reason: {}", skuCode, throwable.getMessage());
        return false;
    }
}
