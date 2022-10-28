package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerEvent;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class SubscriptionClient {

    private final WebSocketGraphQlClient websocketGraphQlClient;

    public Flux<CustomerEvent> subscribeCustomerEvents(){
        return websocketGraphQlClient.documentName("sub")
                .operationName("CustomerEventsSub")
                .retrieveSubscription("customerEvents")
                .toEntity(CustomerEvent.class);
    }

}
