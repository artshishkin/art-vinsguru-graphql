package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Mono<List<CustomerDto>> get2CustomersByIdMy(Integer id1, Integer id2) {
        return this.client.documentName("customer-by-id")
                .operationName("Get2CustomersById")
                .variable("customer1Id", id1)
                .variable("customer2Id", id2)
                .execute()
                .map(cr -> cr.toEntity(new ParameterizedTypeReference<Map<String, CustomerDto>>() {
                }))
                .map(Map::values)
                .map(ArrayList::new);

    }

    public Mono<List<CustomerDto>> get2CustomersByIdSolution1(Integer id1, Integer id2) {
        return this.client.documentName("customer-by-id")
                .operationName("Get2CustomersById")
                .variable("customer1Id", id1)
                .variable("customer2Id", id2)
                .retrieve("")
                .toEntity(MultiCustomerDto.class)
                .map(multi -> List.of(multi.getA(), multi.getB()));
    }

}
