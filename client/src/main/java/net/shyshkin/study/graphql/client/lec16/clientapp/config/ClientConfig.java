package net.shyshkin.study.graphql.client.lec16.clientapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

@Configuration
public class ClientConfig {

    @Bean
    HttpGraphQlClient graphQlClient(@Value("${customer.service.url}") String baseUrl) {
        return HttpGraphQlClient.builder()
                .webClient(b -> b
                        .baseUrl(baseUrl)
                        .defaultHeader("client-id","SomeClientId")
                )
                .build();
    }

    @Bean
    WebSocketGraphQlClient websocketGraphQlClient(@Value("${customer.events.subscription.url}") String baseUrl) {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        return WebSocketGraphQlClient.builder(baseUrl, client)
                .build();
    }

}
