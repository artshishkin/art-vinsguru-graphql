package net.shyshkin.study.graphql.graphqlplayground.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class HelloWorldController {

    @QueryMapping("sayHello")
    public Mono<String> helloWorld() {
        return Mono.just("Hello World!");
    }

    @QueryMapping
    public Mono<String> sayHelloTo(@Argument("name") String value) {
        return Mono.just("Hello " + value + "!");
    }

}
