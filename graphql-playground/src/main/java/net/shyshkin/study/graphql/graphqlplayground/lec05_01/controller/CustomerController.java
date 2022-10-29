package net.shyshkin.study.graphql.graphqlplayground.lec05_01.controller;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.service.CustomerService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @QueryMapping
    public Flux<Customer> customers(DataFetchingEnvironment environment) {
        log.debug("fetch all customers");
        log.debug("customers SelectionSet: {}", environment.getSelectionSet().getFields());
        log.debug("customers Env document: {}, hash: {}", environment.getDocument(), environment.getDocument().hashCode());
        log.debug("customers Env operation: {}", environment.getOperationDefinition());
        return service.getAllCustomers();
    }

}
