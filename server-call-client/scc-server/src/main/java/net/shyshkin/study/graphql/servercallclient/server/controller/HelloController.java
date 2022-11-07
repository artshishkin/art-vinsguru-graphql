package net.shyshkin.study.graphql.servercallclient.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class HelloController {

    @MessageMapping("hello")
    public Mono<Void> hello(Mono<String> name) {
        return name
                .doOnNext(msg -> log.debug("Received: {}", msg))
                .then();
    }
}
