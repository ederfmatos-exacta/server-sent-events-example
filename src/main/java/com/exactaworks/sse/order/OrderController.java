package com.exactaworks.sse.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final Sinks.Many<Order> logInfoSink = Sinks.many().multicast().directAllOrNothing();

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addOrder(@RequestBody Order order) {
        logInfoSink.tryEmitNext(order);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Order> getOrdersStream(@RequestParam("owner") String owner) {
        return logInfoSink.asFlux()
                .filter(order -> order.owner().equals(owner));
    }

}
