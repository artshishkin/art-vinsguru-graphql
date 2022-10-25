package net.shyshkin.study.graphql.crud.lec14.service;

import net.shyshkin.study.graphql.crud.lec14.dto.CustomerEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class CustomerEventService {

    private final Sinks.Many<CustomerEvent> sink = Sinks
            .many()
            .multicast()
            .onBackpressureBuffer();

    private final Flux<CustomerEvent> flux = sink.asFlux().cache(0);

    public Flux<CustomerEvent> subscribe() {
        return flux;
    }

    public void emitEvent(CustomerEvent event) {
        sink.tryEmitNext(event);
    }

}
