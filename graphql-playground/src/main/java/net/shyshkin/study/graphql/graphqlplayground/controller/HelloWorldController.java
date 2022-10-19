package net.shyshkin.study.graphql.graphqlplayground.controller;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class HelloWorldController {

    @QueryMapping
    public Mono<String> sayHello() {
        return Mono.just("Hello World!");
    }

}
