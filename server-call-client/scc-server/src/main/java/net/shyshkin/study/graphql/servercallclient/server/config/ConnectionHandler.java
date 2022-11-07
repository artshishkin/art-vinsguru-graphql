package net.shyshkin.study.graphql.servercallclient.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.server.service.RSocketRequesterManager;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionHandler {

    private final RSocketRequesterManager rSocketRequesterManager;

    @ConnectMapping("movie-app-client")
    Mono<Void> handleConnection(RSocketRequester requester, @Payload UUID requesterId) {
        return Mono.fromRunnable(() -> this.connectRequester(requesterId, requester));
    }

    private void connectRequester(UUID clientId, RSocketRequester requester) {
        Objects
                .requireNonNull(requester.rsocket())
                .onClose() // (1)
                .log()
                .doFirst(() -> {
                    log.debug("Client: {} CONNECTED.", clientId);
                    rSocketRequesterManager.addRequester(clientId, requester); // (2)
                })
                .doOnError(error -> {
                    log.error("Channel to client {} CLOSED", clientId, error); // (3)
                })
                .doFinally(consumer -> {
                    rSocketRequesterManager.removeRequester(clientId, requester);
                    log.debug("Client {} DISCONNECTED", clientId); // (4)
                })
                .subscribe();
    }

}
