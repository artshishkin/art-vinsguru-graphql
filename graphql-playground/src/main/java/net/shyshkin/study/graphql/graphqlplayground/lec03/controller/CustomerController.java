package net.shyshkin.study.graphql.graphqlplayground.lec03.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec03.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec03.dto.CustomerOrderDto;
import net.shyshkin.study.graphql.graphqlplayground.lec03.service.CustomerService;
import net.shyshkin.study.graphql.graphqlplayground.lec03.service.OrderService;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@Profile("lec03")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final OrderService orderService;

//    @QueryMapping
    @SchemaMapping(
            typeName = "Query"
    )
    public Flux<Customer> customers() {
        log.debug("fetch all customers");
        return service.getAllCustomers();
    }

    @SchemaMapping(
            typeName = "Customer"
    )
    public Flux<CustomerOrderDto> orders(Customer customer) {
        log.debug("fetch all orders of {}", customer);
        return orderService.ordersByCustomerId(customer.getId());
    }

}
