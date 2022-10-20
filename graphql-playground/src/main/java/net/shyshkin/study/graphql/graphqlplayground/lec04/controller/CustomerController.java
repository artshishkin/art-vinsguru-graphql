package net.shyshkin.study.graphql.graphqlplayground.lec04.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.CustomerOrderDto;
import net.shyshkin.study.graphql.graphqlplayground.lec04.service.CustomerService;
import net.shyshkin.study.graphql.graphqlplayground.lec04.service.OrderService;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@Profile("lec04")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final OrderService orderService;

    // @QueryMapping
    @SchemaMapping(typeName = "Query")
    public Flux<Customer> customers() {
        log.debug("fetch all customers");
        return service.getAllCustomers();
    }

    // N + 1 problem fix
    @BatchMapping(typeName = "Customer")
    public Flux<List<CustomerOrderDto>> orders(List<Customer> list) {
        log.debug("fetch all orders for {} customers", list.size());
        List<Integer> ids = list.stream()
                .map(Customer::getId)
                .collect(Collectors.toList());
        return orderService.ordersByCustomerIds(ids);
    }



}
