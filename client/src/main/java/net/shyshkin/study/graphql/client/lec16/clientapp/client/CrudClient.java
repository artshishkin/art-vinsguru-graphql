package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CrudClient {

    public static final ParameterizedTypeReference<CustomerDto> CUSTOMER_TYPE = new ParameterizedTypeReference<>() {
    };
    public static final ParameterizedTypeReference<List<CustomerDto>> CUSTOMER_LIST_TYPE = new ParameterizedTypeReference<>() {
    };

    private final HttpGraphQlClient graphQlClient;

    public Mono<List<CustomerDto>> getAllCustomers() {
        return crud("AllCustomers", Map.of(), CUSTOMER_LIST_TYPE);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return crud("CustomerById", Map.of("customerId", id), CUSTOMER_TYPE);
    }

    public Mono<CustomerDto> createNewCustomer(CustomerDto customerDto) {
        return crud("CreateCustomer", Map.of("customerInput", customerDto), CUSTOMER_TYPE);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, CustomerDto customerDto) {
        customerDto.setId(null);
        return crud("UpdateCustomer", Map.of("customerInput", customerDto, "customerId", id), CUSTOMER_TYPE);
    }

    private <T> Mono<T> crud(String operationName, Map<String, Object> variables, ParameterizedTypeReference<T> type) {
        return graphQlClient.documentName("crud")
                .operationName(operationName)
                .variables(variables)
                .retrieve("response")
                .toEntity(type);

    }

}
