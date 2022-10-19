package net.shyshkin.study.graphql.graphqlplayground.lec02.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.AgeRangeFilter;
import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec02.service.CustomerService;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@Profile("lec02")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @QueryMapping
    public Flux<Customer> customers() {
        log.debug("fetch all customers");
        return service.getAllCustomers();
    }

    @QueryMapping
    public Mono<Customer> customerById(@Argument Integer id) {
        log.debug("fetch customer by id");
        return service.getCustomerById(id);
    }

    @QueryMapping
    public Flux<Customer> customerNameContains(@Argument String name) {
        log.debug("filter customers by name");
        return service.customerNameContains(name);
    }

    @QueryMapping
    public Flux<Customer> customersByAgeRange(@Argument AgeRangeFilter filter) {
        log.debug("filter customers by age range");
        return service.getCustomersWithinAge(filter);
    }

}
