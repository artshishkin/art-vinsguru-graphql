package net.shyshkin.study.graphql.servercallclient.server.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.server.client.CustomRSocketGraphQlClientBuilder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PingService {

    private final RSocketRequesterManager rSocketRequesterManager;

    public Mono<String> ping(UUID requesterId) {

        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);

        return Mono.justOrEmpty(requesterOptional)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
//                                .document("query{ping}")
                                .documentName("ping")
                                .retrieve("ping")
                                .toEntity(String.class)
                );
    }

}
