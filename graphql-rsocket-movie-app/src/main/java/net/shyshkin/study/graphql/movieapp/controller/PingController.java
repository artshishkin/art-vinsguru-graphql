package net.shyshkin.study.graphql.movieapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class PingController {

    @QueryMapping
    public Mono<String> ping() {
        log.debug("ping");
        return Mono.just("pong");
    }
}
