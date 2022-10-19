package net.shyshkin.study.graphql.graphqlplayground.lec01.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Controller
@Profile("lec01")
public class HelloWorldController {

    @QueryMapping("sayHello")
    public Mono<String> helloWorld() {
        log.debug("Executing sayHello...");
        return Mono.just("Hello World!")
                .doOnNext(msg -> log.debug("sayHello executed with: {}", msg));
    }

    @QueryMapping
    public Mono<String> sayHelloTo(@Argument("name") String value) {
        log.debug("Executing sayHelloTo...");
        return Mono.just("Hello " + value + "!")
                .doOnNext(msg -> log.debug("sayHelloTo executed with: {}", msg));
    }

    @QueryMapping
    public Mono<Integer> random() {
        log.debug("Executing random...");
        return Mono.just(ThreadLocalRandom.current().nextInt(1, 100))
                .doOnNext(msg -> log.debug("random executed with: {}", msg));
    }

}
