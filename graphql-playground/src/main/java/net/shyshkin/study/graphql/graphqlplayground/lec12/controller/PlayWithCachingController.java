package net.shyshkin.study.graphql.graphqlplayground.lec12.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class PlayWithCachingController {

    @QueryMapping
    public Mono<String> sayHello(@Argument("name") String value) {
        log.debug("Executing sayHello...");
        return Mono.fromSupplier(() -> "Hello " + value + "!")
                .doOnNext(msg -> log.debug("sayHello executed with: {}", msg));
    }

}
