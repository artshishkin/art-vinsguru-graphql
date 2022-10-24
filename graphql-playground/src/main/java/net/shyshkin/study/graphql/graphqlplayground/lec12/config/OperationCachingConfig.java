package net.shyshkin.study.graphql.graphqlplayground.lec12.config;

import graphql.ExecutionInput;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec12.service.CacheMonitorFakeService;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Profile("lec12")
@Configuration
@RequiredArgsConstructor
public class OperationCachingConfig {

    private final CacheMonitorFakeService cacheMonitorFakeService;

    @Bean
    GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
        return c -> c.configureGraphQl(
                b -> b.preparsedDocumentProvider(cachedPreparsedDocumentProvider())
        );
    }

    @Bean
    PreparsedDocumentProvider cachedPreparsedDocumentProvider() {
        Map<String, PreparsedDocumentEntry> cache = new ConcurrentHashMap<>();
        return new PreparsedDocumentProvider() {
            @Override
            public PreparsedDocumentEntry getDocument(ExecutionInput executionInput, Function<ExecutionInput, PreparsedDocumentEntry> parseAndValidateFunction) {
                return cache.computeIfAbsent(
                        executionInput.getQuery(),
                        q -> {
                            log.debug("Not found in cache: {}", q);
                            PreparsedDocumentEntry preparsedDocumentEntry = parseAndValidateFunction.apply(executionInput);
                            log.debug("Caching: {}", preparsedDocumentEntry);
                            cacheMonitorFakeService.confirmParsingAndValidation();
                            return preparsedDocumentEntry;
                        }
                );
            }
        };
    }

}
