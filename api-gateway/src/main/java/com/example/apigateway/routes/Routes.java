package com.example.apigateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
@Slf4j
public class Routes {

    @Value("${services.approval-url}")
    private String approvalServiceUrl;

    @Value("${services.booking-url}")
    private String bookingServiceUrl;

    @Value("${services.event-url}")
    private String eventServiceUrl;

    @Value("${services.room-url}")
    private String roomServiceUrl;

    @Value("${services.user-url}")
    private String userServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> approvalServiceRoute() {
        log.info("Initializing approval-service route with URL: {}", approvalServiceUrl);
        return route("approval_service")
                .route(RequestPredicates.path("/api/approval"), request -> {
                    log.info("Received request for approval-service: {}", request.uri());
                    return HandlerFunctions.http(approvalServiceUrl).handle(request);
                })
                .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("approvalServiceCircuitBreaker", URI.create("forward:/fallbackRoute") ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> bookingServiceRoute() {
        log.info("Initializing booking-service route with URL: {}", bookingServiceUrl);
        return route("booking_service")
                .route(RequestPredicates.path("/api/booking"), request -> {
                    log.info("Received request for booking-service: {}", request.uri());
                    return HandlerFunctions.http(bookingServiceUrl).handle(request);
                })
                .filter(CircuitBreakerFilterFunctions
                .circuitBreaker("bookingServiceCircuitBreaker", URI.create("forward:/fallbackRoute") ))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> eventServiceRoute() {
        log.info("Initializing event-service route with URL: {}", eventServiceUrl);
        return route("event_service")
                .route(RequestPredicates.path("/api/event"), request -> {
                    log.info("Received request for event-service: {}", request.uri());
                    try {
                        ServerResponse response = HandlerFunctions.http(eventServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred while routing to event-service");
                    }
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> roomServiceRoute() {
        log.info("Initializing room-service route with URL: {}", roomServiceUrl);
        return route("room_service")
                .route(RequestPredicates.path("/api/room"), request -> {
                    log.info("Received request for room-service: {}", request.uri());
                    try {
                        ServerResponse response = HandlerFunctions.http(roomServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred while routing to room-service");
                    }
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        log.info("Initializing user-service route with URL: {}", userServiceUrl);
        return route("user_service")
                .route(RequestPredicates.path("/api/user"), request -> {
                    log.info("Received request for user-service: {}", request.uri());
                    try {
                        ServerResponse response = HandlerFunctions.http(userServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred while routing to user-service");
                    }
                })
                .build();
    }

    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackRoute")
                .route(RequestPredicates.all(),
                        request ->ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Service is temporarily unavailable, please try again later"))
                .build();
    }
}
