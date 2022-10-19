package net.shyshkin.study.graphql.graphqlplayground.lec02.service;

import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.AgeRangeFilter;
import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Profile("lec02")
public class CustomerService {

    private final List<Customer> customers = IntStream.rangeClosed(1, 5)
            .boxed()
            .map(this::mockCustomer)
            .collect(Collectors.toList());

    private final Flux<Customer> flux = Flux.fromIterable(customers);

    public Flux<Customer> getAllCustomers() {
        return flux;
    }

    public Mono<Customer> getCustomerById(Integer id) {
        return flux
                .filter(customer -> Objects.equals(customer.getId(), id))
                .next();
    }

    public Flux<Customer> customerNameContains(String name) {
        String lowerName = name.toLowerCase();
        return flux
                .filter(customer -> customer.getName().toLowerCase().contains(lowerName));
    }

    public Flux<Customer> getCustomersWithinAge(AgeRangeFilter filter) {
        return flux
                .filter(customer -> customer.getAge() >= filter.getMinAge() && customer.getAge() <= filter.getMaxAge());
    }

    private Customer mockCustomer(Integer id) {
        return Customer.builder()
                .id(id)
                .name(String.format("Customer_%02d", id))
                .age(ThreadLocalRandom.current().nextInt(18, 60))
                .city(String.format("City%02d", id))
                .build();
    }

}
