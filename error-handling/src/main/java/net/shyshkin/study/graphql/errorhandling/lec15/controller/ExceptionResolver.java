package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import net.shyshkin.study.graphql.errorhandling.lec15.exception.ApplicationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class ExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception, DataFetchingEnvironment environment) {
        ApplicationException ex = toApplicationException(exception);
        return Mono.just(List.of(
                GraphqlErrorBuilder.newError()
                        .path(environment.getExecutionStepInfo().getPath())
                        .message(ex.getMessage())
                        .errorType(ex.getErrorType())
                        .extensions(ex.getExtensions())
                        .build()
        ));
    }

    private ApplicationException toApplicationException(Throwable throwable) {
        return (throwable instanceof ApplicationException) ?
                (ApplicationException) throwable :
                new ApplicationException(ErrorType.INTERNAL_ERROR, throwable.getMessage(), Collections.emptyMap());
    }

}
