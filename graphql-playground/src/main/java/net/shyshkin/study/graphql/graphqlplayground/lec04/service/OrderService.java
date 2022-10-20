package net.shyshkin.study.graphql.graphqlplayground.lec04.service;

import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.CustomerOrderDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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
                .map(customerId -> db.getOrDefault(customerId, List.of()));
    }

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
