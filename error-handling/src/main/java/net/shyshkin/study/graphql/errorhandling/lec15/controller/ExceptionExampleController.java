package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import net.shyshkin.study.graphql.errorhandling.lec15.exception.AppException;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
public class ExceptionExampleController {

    @QueryMapping
    public Mono<String> unhandledException(){
        throw new RuntimeException("Some Weird Issue");
    }

    @QueryMapping
    public Mono<String> appCustomException(){
        throw new AppException("App Custom Weird Issue");
    }

}
