package net.shyshkin.study.graphql.servercallclient.client.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
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
        if (throwable instanceof ApplicationException)
            return (ApplicationException) throwable;
        return new ApplicationException(ErrorType.INTERNAL_ERROR, throwable.getMessage(), Collections.emptyMap());
    }

}
