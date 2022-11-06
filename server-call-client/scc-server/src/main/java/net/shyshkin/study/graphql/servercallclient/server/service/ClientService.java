package net.shyshkin.study.graphql.servercallclient.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ClientService {

    private final Map<UUID, RSocketRequester> CLIENTS = new HashMap<>();

    public void addClient(UUID clientId, RSocketRequester client) {
        CLIENTS.put(clientId, client);
    }

    public void removeClient(UUID clientId, RSocketRequester client) {
        CLIENTS.remove(clientId);
    }

    public Optional<RSocketRequester> getClient(UUID clientId) {
        return Optional.ofNullable(CLIENTS.get(clientId));
    }

}
