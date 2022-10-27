package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Service
@Profile("header-check-filter")
public class HeaderCheckFilter implements WebFilter {

    @Value("${app.client-id-header}")
    private String clientIdHeaderName;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String clientId = exchange.getRequest().getHeaders().getFirst(clientIdHeaderName);

        return (clientId != null) ?
                chain.filter(exchange) :
                Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST));
    }
}
