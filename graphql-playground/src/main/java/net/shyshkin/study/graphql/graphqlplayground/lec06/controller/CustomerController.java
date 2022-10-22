package net.shyshkin.study.graphql.graphqlplayground.lec06.controller;

import graphql.schema.DataFetchingFieldSelectionSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec06.dto.CustomerWithOrdersDto;
import net.shyshkin.study.graphql.graphqlplayground.lec06.service.CustomerOrderDataFetcher;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@Profile("lec06")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerOrderDataFetcher customerOrderDataFetcher;

    @QueryMapping
    public Flux<CustomerWithOrdersDto> customers(DataFetchingFieldSelectionSet selectionSet) {
        log.debug("fetch all customers");
        return customerOrderDataFetcher.customerOrders(selectionSet);
    }

}
