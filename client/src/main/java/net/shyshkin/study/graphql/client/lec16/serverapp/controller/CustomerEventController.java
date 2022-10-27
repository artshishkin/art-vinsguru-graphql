package net.shyshkin.study.graphql.client.lec16.serverapp.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerEvent;
import net.shyshkin.study.graphql.client.lec16.serverapp.service.CustomerEventService;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class CustomerEventController {

    private final CustomerEventService customerEventService;

    @SubscriptionMapping
    public Flux<CustomerEvent> customerEvents() {
        return customerEventService.subscribe();
    }

}
