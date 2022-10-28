package net.shyshkin.study.graphql.client.lec16.serverapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {

        String clientId = request.getHeaders().getFirst("client-id");
        if (clientId != null) log.debug("ClientID: {}", clientId);

        String customerId = request.getHeaders().getFirst("X-CUSTOMER-ID");
        if (customerId != null) log.debug("Customer ID: {}", customerId);

        return chain.next(request);
    }
}
