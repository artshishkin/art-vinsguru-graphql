package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import net.shyshkin.study.graphql.errorhandling.lec15.exception.ApplicationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (throwable instanceof ConstraintViolationException) {
            ConstraintViolationException ex = (ConstraintViolationException) throwable;

            Set<Object> objects = ex.getConstraintViolations().stream()
                    .map(ConstraintViolation::getLeafBean)
                    .collect(Collectors.toSet());
            Map<String, Object> extensions;
            if (objects.size() == 1 && objects.iterator().next() instanceof CustomerDto)
                extensions = Map.of("input", objects.iterator().next());
            else
                extensions = Map.of("input", objects);
            return new ApplicationException(ErrorType.BAD_REQUEST, throwable.getMessage(), extensions);
        }
        return new ApplicationException(ErrorType.INTERNAL_ERROR, throwable.getMessage(), Collections.emptyMap());
    }

}
