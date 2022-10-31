package net.shyshkin.study.graphql.movieapp.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.movieapp.client.CustomerClient;
import net.shyshkin.study.graphql.movieapp.dto.Customer;
import net.shyshkin.study.graphql.movieapp.dto.CustomerInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerClient customerClient;

    @QueryMapping("userProfile")
    public Mono<Customer> getUserProfile(@Argument Integer id) {
        return customerClient.getCustomerById(id);
    }

    @MutationMapping
    public Mono<Customer> setProfile(@Argument CustomerInput input) {
        return customerClient.updateCustomer(input);
    }

}
