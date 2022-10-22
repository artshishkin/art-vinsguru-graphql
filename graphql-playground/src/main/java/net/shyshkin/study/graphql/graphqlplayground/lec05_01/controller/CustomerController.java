package net.shyshkin.study.graphql.graphqlplayground.lec05_01.controller;

import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.service.CustomerService;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@Profile("lec05_01")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @QueryMapping
    public Flux<Customer> customers(DataFetchingFieldSelectionSet selectionSet) {
        log.debug("fetch all customers");
        log.debug("customers: {}", selectionSet.getFields());
        return service.getAllCustomers();
    }

}
