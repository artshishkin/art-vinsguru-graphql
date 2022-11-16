package net.shyshkin.study.graphql.servercallclient.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PingService {

    private final RSocketGraphQlClientManager rSocketGraphQlClientManager;

    public Mono<String> ping(UUID requesterId) {

        return Mono.justOrEmpty(rSocketGraphQlClientManager.getGraphQlClient(requesterId))
                .flatMap(client -> client
//                                .document("query{ping}")
                                .documentName("ping")
                                .retrieve("ping")
                                .toEntity(String.class)
                );
    }

}
