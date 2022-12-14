package net.shyshkin.study.graphql.graphqlplayground.lec07.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec07.dto.CustomerOrderDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class OrderService {

    private final Map<Integer, List<CustomerOrderDto>> db = createDB();

    public Flux<CustomerOrderDto> ordersByCustomerId(Integer customerId) {
        return Flux.fromIterable(db.getOrDefault(customerId, List.of()))
                .delayElements(Duration.ofMillis(200))
                .doOnNext(o -> log.debug("Order for customer {}: {}", customerId, o));
    }

    private Map<Integer, List<CustomerOrderDto>> createDB() {
        return IntStream.rangeClosed(1, 3)
                .boxed()
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
