package net.shyshkin.study.graphql.graphqlplayground.lec05.service;

import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Customer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CustomerService {

    private final List<Customer> customers = IntStream.rangeClosed(1, 5)
            .boxed()
            .map(this::mockCustomer)
            .collect(Collectors.toList());

    private final Flux<Customer> flux = Flux.fromIterable(customers);

    public Flux<Customer> getAllCustomers() {
        return flux;
    }

    private Customer mockCustomer(Integer id) {
        return Customer.builder()
                .id(id)
                .name(String.format("Customer_%02d", id))
                .age(ThreadLocalRandom.current().nextInt(18, 60))
                .build();
    }

}
