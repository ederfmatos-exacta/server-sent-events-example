package com.exactaworks.sse.order;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class OrderHandler {

    private final Sinks.Many<Order> logInfoSink = Sinks.many().replay().latest();

    public Mono<ServerResponse> addOrder(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Order.class)
                .doOnNext(logInfoSink::tryEmitNext)
                .flatMap(order -> ServerResponse.noContent().build())
                .log();
    }

    public Mono<ServerResponse> getOrdersStream(ServerRequest request) {
        String owner = request.queryParam("owner").orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(logInfoSink.asFlux().filter(order -> order.owner().equals(owner)), Order.class)
                .log();
    }

}
