package net.shyshkin.study.graphql.graphqlplayground.lec04.service;

import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.CustomerOrderDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Profile("lec04")
public class OrderService {

    private final Map<Integer, List<CustomerOrderDto>> db = createDB();

    public Flux<CustomerOrderDto> ordersByCustomerId(Integer customerId) {
        return Flux.fromIterable(db.getOrDefault(customerId, List.of()));
    }

    public Flux<List<CustomerOrderDto>> ordersByCustomerIds(List<Integer> list) {
        return Flux.fromIterable(list)
                .flatMapSequential(id -> fetchOrders(id).defaultIfEmpty(List.of()));
    }

    // some source
    private Mono<List<CustomerOrderDto>> fetchOrders(Integer id) {
        return Mono.justOrEmpty(db.get(id))
                .delayElement(Duration.ofMillis(ThreadLocalRandom.current().nextInt(500)));
    }

    public Mono<Map<Customer, List<CustomerOrderDto>>> fetchOrdersAsMap(List<Customer> customers) {
        return Flux.fromIterable(customers)
                .flatMap(customer -> fetchOrders(customer.getId())
                        .defaultIfEmpty(List.of())
                        .map(list -> Tuples.of(customer, list))
                )
                .collectMap(Tuple2::getT1, Tuple2::getT2);
    }

//    public Mono<Map<Customer, List<CustomerOrderDto>>> fetchOrdersAsMap(List<Customer> customers) {
//        return Flux.fromIterable(customers)
//                .flatMap(customer -> fetchOrders(customer.getId())
//                        .defaultIfEmpty(List.of())
//                        .map(list -> Map.entry(customer, list))
//                )
//                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
//    }

    private Map<Integer, List<CustomerOrderDto>> createDB() {
        return IntStream.rangeClosed(1, 5)
                .boxed()
                .filter(i -> i % 2 == 1)
                .collect(Collectors.toMap(Function.identity(), this::mockCustomerOrders));
    }

    private List<CustomerOrderDto> mockCustomerOrders(Integer customerId) {
        return IntStream.rangeClosed(1, 5)
                .boxed()
                .map(orderIndex -> mockOrder(customerId, orderIndex))
                .collect(Collectors.toList());
    }

    private CustomerOrderDto mockOrder(Integer customerId, Integer orderIndex) {
        return CustomerOrderDto.builder()
                .id(UUID.randomUUID())
                .description(String.format("Order_%02d_%02d", customerId, orderIndex))
                .build();
    }

}
