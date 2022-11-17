package net.shyshkin.study.graphql.servercallclient.server.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SuppressFBWarnings("EI_EXPOSE_REP2")
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
