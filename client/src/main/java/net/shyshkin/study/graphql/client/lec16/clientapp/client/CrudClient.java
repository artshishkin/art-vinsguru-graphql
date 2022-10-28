package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class CrudClient {

    private final HttpGraphQlClient graphQlClient;

    public Flux<CustomerDto> getAllCustomers() {
        return graphQlClient.documentName("crud")
                .operationName("AllCustomers")
                .retrieve("customers")
                .toEntityList(CustomerDto.class)
                .flatMapIterable(Function.identity());
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return graphQlClient.documentName("crud")
                .operationName("CustomerById")
                .variable("customerId", id)
                .retrieve("customerById")
                .toEntity(CustomerDto.class);
    }

}
