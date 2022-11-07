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
public class RSocketRequesterManager {

    private final Map<UUID, RSocketRequester> RSOCKET_REQUESTERS = new HashMap<>();

    public void addRequester(UUID clientId, RSocketRequester requester) {
        RSOCKET_REQUESTERS.put(clientId, requester);
    }

    public void removeRequester(UUID clientId, RSocketRequester requester) {
        RSOCKET_REQUESTERS.remove(clientId);
    }

    public Optional<RSocketRequester> getRequester(UUID requesterId) {
        return Optional.ofNullable(RSOCKET_REQUESTERS.get(requesterId));
    }

}
