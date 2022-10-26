package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerNotFound;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.DeleteResultDto;
import net.shyshkin.study.graphql.errorhandling.lec15.service.CustomerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @Value("${app.mutation.delay:2s}")
    private Duration appMutationDelay;

    @QueryMapping
    public Flux<CustomerDto> customers() {
        return service.getAllCustomers();
    }

    @QueryMapping
    public Mono<CustomerDto> customerById(@Argument Integer id) {
        return service.getCustomerById(id);
    }

    @QueryMapping
    public Mono<Object> customerByIdUnion(@Argument Integer id) {
        return service.getCustomerByIdNoError(id)
                .cast(Object.class)
                .switchIfEmpty(Mono.just(CustomerNotFound.builder()
                        .id(id)
                        .build())
                );
    }

    @MutationMapping
    public Mono<CustomerDto> createCustomer(@Argument @Valid CustomerDto customer) {
        log.debug("Creating new Customer {}...", customer);
        return service.createCustomer(customer)
                .delayElement(appMutationDelay)
                .doOnNext(c -> log.debug("Customer created: {}", c));
    }

    @MutationMapping
    public Mono<CustomerDto> updateCustomer(@Argument Integer id, @Argument("customer") CustomerDto dto) {
        log.debug("Updating Customer with id {}...", id);
        return service.updateCustomer(id, dto)
                .delayElement(appMutationDelay)
                .doOnNext(c -> log.debug("Customer updated: {}", c));
    }

    @MutationMapping
    public Mono<DeleteResultDto> deleteCustomer(@Argument Integer id) {
        return service.deleteCustomer(id);
    }

}
