package net.shyshkin.study.graphql.graphqlplayground.lec05.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Address;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Customer;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class AddressController {

//    @SchemaMapping(typeName = "Customer") //we can skip typeName if we have it in method arguments
    @SchemaMapping
    public Mono<Address> address(Customer customer) {
        log.debug("fetch address of {}", customer);
        return Mono.fromSupplier(() -> Address.builder()
                .city("City" + customer.getId())
                .street("Street" + customer.getId())
                .build());
    }

}
