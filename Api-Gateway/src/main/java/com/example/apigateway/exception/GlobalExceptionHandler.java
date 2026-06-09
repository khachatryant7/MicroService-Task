package com.example.apigateway.exception;

import jakarta.ws.rs.NotFoundException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

@Component
@Order(0)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = (ServerHttpResponse) exchange.getResponse();

        HttpStatus httpStatus;
        String message = "";

        if (ex instanceof NotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            message = "Error, route not found";
        } else if (ex instanceof ResponseStatusException res) {
            httpStatus = HttpStatus.valueOf(res.getStatusCode().value());
        } else if (ex instanceof ConnectException) {
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            message = " Error, Service is unavailable";
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Error, internal server error";
        }
        response.setStatusCode(httpStatus);

        String body = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                httpStatus.value(), httpStatus.getReasonPhrase(), message
        );


        return response.writeWith(Mono.fromCallable(() ->
                response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))
        ));
    }
}