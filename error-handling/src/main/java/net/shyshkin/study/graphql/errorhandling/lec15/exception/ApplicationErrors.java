package net.shyshkin.study.graphql.errorhandling.lec15.exception;

import org.springframework.graphql.execution.ErrorType;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ApplicationErrors {

    public static <T> Mono<T> noSuchCustomer(Integer id) {
        return Mono.error(new ApplicationException(
                ErrorType.BAD_REQUEST,
                "No such customer",
                Map.of(
                        "customerId", id
                )
        ));
    }
}
