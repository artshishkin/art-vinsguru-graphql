package net.shyshkin.study.graphql.client.lec16.clientapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;

@Configuration
public class ClientConfig {

    @Bean
    HttpGraphQlClient graphQlClient(@Value("${customer.service.url}") String baseUrl) {
        return HttpGraphQlClient.builder()
                .webClient(b -> b.baseUrl(baseUrl))
                .build();
    }
}
