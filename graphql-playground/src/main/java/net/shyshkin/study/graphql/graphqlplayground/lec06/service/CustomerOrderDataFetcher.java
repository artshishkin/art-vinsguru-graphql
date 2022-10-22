package net.shyshkin.study.graphql.graphqlplayground.lec06.service;

import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec06.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec06.dto.CustomerWithOrdersDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

@Slf4j
@Service
@Profile("lec06")
@RequiredArgsConstructor
public class CustomerOrderDataFetcher {

    private final CustomerService customerService;
    private final OrderService orderService;

    public Flux<CustomerWithOrdersDto> customerOrders(DataFetchingFieldSelectionSet selectionSet) {
        log.debug("Custom Fetcher Service: fetch all customers");
        boolean includeOrders = selectionSet.contains("orders");
        return customerService.getAllCustomers()
                .map(this::toRestDto)
                .transform(updateOrdersTransformer(includeOrders));
    }

    private UnaryOperator<Flux<CustomerWithOrdersDto>> updateOrdersTransformer(boolean includeOrders) {
        return includeOrders ?
                f -> f.flatMapSequential(this::fetchOrders) :
                f -> f;
    }

    private Mono<CustomerWithOrdersDto> fetchOrders(CustomerWithOrdersDto dto) {
        return orderService.ordersByCustomerId(dto.getId())
                .collectList()
                .doOnNext(dto::setOrders)
                .thenReturn(dto);
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
