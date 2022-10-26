package net.shyshkin.study.graphql.errorhandling.lec15.exception;

import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
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

    public static <T> Mono<T> below18Years(CustomerDto customerDto) {
        return Mono.error(new ApplicationException(
                ErrorType.BAD_REQUEST,
                "Age must be greater then 18",
                Map.of(
                        "input", customerDto
                )
        ));
    }
}
