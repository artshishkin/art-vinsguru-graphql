package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import net.shyshkin.study.graphql.errorhandling.lec15.exception.AppException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        if (exception instanceof AppException)
            return Mono.just(List.of(
                    GraphqlErrorBuilder.newError(environment)
                            .message(exception.getMessage())
                            .errorType(ErrorType.INTERNAL_ERROR)
                            .build()
            ));
        //default error resolver
        return Mono.empty();
    }

}
