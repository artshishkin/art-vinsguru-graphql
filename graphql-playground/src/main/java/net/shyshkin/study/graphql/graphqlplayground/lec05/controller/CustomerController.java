package net.shyshkin.study.graphql.graphqlplayground.lec05.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec05.service.CustomerService;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@Profile("lec05")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @QueryMapping
    public Flux<Customer> customers() {
        log.debug("fetch all customers");
        return service.getAllCustomers();
    }

}
