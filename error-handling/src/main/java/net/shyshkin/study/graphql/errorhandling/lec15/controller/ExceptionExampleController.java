package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import net.shyshkin.study.graphql.errorhandling.lec15.exception.ApplicationException;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class ExceptionExampleController {

    @QueryMapping
    public Mono<String> unhandledException() {
        throw new RuntimeException("Some Weird Issue");
    }

    @QueryMapping
    public Mono<String> appCustomException() {
        throw new ApplicationException(
                ErrorType.INTERNAL_ERROR,
                "App Custom Weird Issue",
                Map.of(
                        "customerId", 123,
                        "timestamp", LocalDateTime.now(),
                        "foo", "bar"
                ));
    }

}
