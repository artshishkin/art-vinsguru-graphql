package net.shyshkin.study.graphql.graphqlplayground.lec06.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec06.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec06.dto.CustomerWithOrdersDto;
import net.shyshkin.study.graphql.graphqlplayground.lec06.service.CustomerService;
import net.shyshkin.study.graphql.graphqlplayground.lec06.service.OrderService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@Profile("lec06")
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerRestController {

    private final CustomerService service;
    private final OrderService orderService;

    @GetMapping
    public Flux<CustomerWithOrdersDto> customers() {
        log.debug("REST fetch all customers");
        return service.getAllCustomers()
                .map(this::toRestDto)
                .flatMapSequential(dto -> orderService.ordersByCustomerId(dto.getId())
                        .collectList()
                        .doOnNext(dto::setOrders)
                        .thenReturn(dto)
                );
    }

    private CustomerWithOrdersDto toRestDto(Customer customer) {
        return CustomerWithOrdersDto.builder()
                .id(customer.getId())
                .age(customer.getAge())
                .city(customer.getCity())
                .name(customer.getName())
                .build();
    }
}
