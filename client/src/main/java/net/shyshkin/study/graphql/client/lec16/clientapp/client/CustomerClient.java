package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.client.lec16.clientapp.dto.GenericResponse;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerNotFoundDto;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomerClient {

    private final HttpGraphQlClient client;

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

    public Mono<GenericResponse<CustomerDto>> getCustomerByIdGenericResponse(Integer id) {
        return this.client.documentName("customer-by-id")
                .operationName("GetCustomerById")
                .variable("customerId", id)
                .execute()
                .map(cr -> {
                    var field = cr.field("customerById");
                    var b = GenericResponse.<CustomerDto>builder();
                    if (field.hasValue())
                        b.data(field.toEntity(CustomerDto.class));
                    else
                        b.error(field.getError());
                    return b.build();
                });
    }

    public Mono<CustomerResponse> getCustomerByIdUnion(Integer id) {
        return this.client.documentName("customer-by-id")
                .operationName("GetCustomerByIdUnion")
                .variable("customerId", id)
                .execute()
                .map(cr -> {
                    var respType = cr.field("customerByIdUnion.__typename").toEntity(String.class);
                    switch (respType) {
                        case "Customer":
                            return cr.field("customerByIdUnion").toEntity(CustomerDto.class);
                        case "CustomerNotFound":
                            return cr.field("customerByIdUnion").toEntity(CustomerNotFoundDto.class);
                        default:
                            throw new RuntimeException("Wrong type: " + respType);
                    }
                });
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
