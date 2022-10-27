package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerClient {

    private final HttpGraphQlClient client;

    public CustomerClient(@Value("${customer.service.url}") String baseUrl) {
        this.client = HttpGraphQlClient.builder()
                .webClient(b -> b.baseUrl(baseUrl))
                .build();
    }

    public Mono<ClientGraphQlResponse> rawQuery(String query) {
        return this.client.document(query)
                .execute();
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return this.client.documentName("customer-by-id")
                .operationName("GetCustomerById")
                .variable("customerId", id)
                .retrieve("customerById")
                .toEntity(CustomerDto.class);
    }

}
