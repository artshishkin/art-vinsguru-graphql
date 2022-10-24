package net.shyshkin.study.graphql.crud.lec13.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.crud.lec13.dto.CustomerDto;
import net.shyshkin.study.graphql.crud.lec13.dto.DeleteResultDto;
import net.shyshkin.study.graphql.crud.lec13.service.CustomerService;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Profile("lec13")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @QueryMapping
    public Flux<CustomerDto> customers() {
        return service.getAllCustomers();
    }

    @QueryMapping
    public Mono<CustomerDto> customerById(@Argument Integer id) {
        return service.getCustomerById(id);
    }

    @MutationMapping
    public Mono<CustomerDto> createCustomer(@Argument CustomerDto customer) {
        return service.createCustomer(customer);
    }

    @MutationMapping
    public Mono<CustomerDto> updateCustomer(@Argument Integer id, @Argument("customer") CustomerDto dto) {
        return service.updateCustomer(id, dto);
    }

    @MutationMapping
    public Mono<DeleteResultDto> deleteCustomer(@Argument Integer id) {
        return service.deleteCustomer(id);
    }

}
