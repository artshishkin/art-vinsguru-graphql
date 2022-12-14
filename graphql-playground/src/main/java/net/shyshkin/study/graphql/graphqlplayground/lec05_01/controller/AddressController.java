package net.shyshkin.study.graphql.graphqlplayground.lec05_01.controller;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Address;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Customer;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class AddressController {

    //    @SchemaMapping(typeName = "Customer") //we can skip typeName if we have it in method arguments
    @SchemaMapping
    public Mono<Address> address(Customer customer, DataFetchingFieldSelectionSet selectionSet, DataFetchingEnvironment environment) {
        log.debug("fetch address of {}", customer);
        log.debug("address selection set: {}", selectionSet.getFields());
        log.debug("address Env document: {}, hash: {}", environment.getDocument(), environment.getDocument().hashCode());
        log.debug("address Env operation: {}", environment.getOperationDefinition());
        return Mono.fromSupplier(() -> Address.builder()
                .city("City" + customer.getId())
                .street("Street" + customer.getId())
                .build());
    }

}
