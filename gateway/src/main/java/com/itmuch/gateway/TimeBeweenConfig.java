package com.itmuch.gateway;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalTime;

@Data
public class TimeBeweenConfig {
    private LocalTime start;
    private LocalTime end;


}
