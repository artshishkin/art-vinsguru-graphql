package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class RequestInterceptor implements WebGraphQlInterceptor {

    @Value("${app.caller-id-header}")
    private String callerIdHeaderName;

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {

        List<String> callerList = request.getHeaders().getOrEmpty(callerIdHeaderName);
        String callerId = callerList.isEmpty() ? "" : callerList.get(0);

        request.configureExecutionInput((executionInput, builder) ->
                builder.graphQLContext(b -> b.of("caller-id", callerId)).build()
        );

        return chain.next(request);
    }
}
