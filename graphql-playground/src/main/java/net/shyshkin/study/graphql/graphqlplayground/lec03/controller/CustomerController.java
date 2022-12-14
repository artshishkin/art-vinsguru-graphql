package net.shyshkin.study.graphql.graphqlplayground.lec03.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec03.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec03.dto.CustomerOrderDto;
import net.shyshkin.study.graphql.graphqlplayground.lec03.service.CustomerService;
import net.shyshkin.study.graphql.graphqlplayground.lec03.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
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
    public Flux<CustomerOrderDto> orders(Customer customer, @Argument("limit") Integer maxSize) {
        log.debug("fetch all orders of {}", customer);
        Flux<CustomerOrderDto> flux = orderService.ordersByCustomerId(customer.getId());
        if (maxSize != null) flux = flux.take(maxSize);
        return flux;
    }

}
