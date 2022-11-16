package net.shyshkin.study.graphql.servercallclient.server.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.server.client.CustomRSocketGraphQlClientBuilder;
import org.springframework.graphql.client.RSocketGraphQlClient;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RSocketGraphQlClientManager {

    private final Map<UUID, RSocketGraphQlClient> GRAPHQL_CLIENTS = new HashMap<>();

    public void addRequester(UUID clientId, RSocketRequester requester) {
        GRAPHQL_CLIENTS.put(
                clientId,
                new CustomRSocketGraphQlClientBuilder(requester).build()
        );
    }

    public void removeRequester(UUID clientId, RSocketRequester requester) {
        GRAPHQL_CLIENTS.remove(clientId);
    }

    public Optional<RSocketGraphQlClient> getGraphQlClient(UUID requesterId) {
        return Optional.ofNullable(GRAPHQL_CLIENTS.get(requesterId));
    }

}
