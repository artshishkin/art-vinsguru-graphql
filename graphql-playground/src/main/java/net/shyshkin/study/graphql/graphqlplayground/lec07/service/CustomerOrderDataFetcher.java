package net.shyshkin.study.graphql.graphqlplayground.lec07.service;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec07.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec07.dto.CustomerWithOrdersDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

@Slf4j
@Service
@Profile("lec07")
@RequiredArgsConstructor
public class CustomerOrderDataFetcher implements DataFetcher<Flux<CustomerWithOrdersDto>> {

    private final CustomerService customerService;
    private final OrderService orderService;

    @Override
    public Flux<CustomerWithOrdersDto> get(DataFetchingEnvironment environment) throws Exception {
        return customerOrders(environment.getSelectionSet());
    }

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
