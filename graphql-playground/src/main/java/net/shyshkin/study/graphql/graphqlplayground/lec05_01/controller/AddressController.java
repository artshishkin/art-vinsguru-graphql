package net.shyshkin.study.graphql.graphqlplayground.lec05_01.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Address;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@Profile("lec05_01")
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
