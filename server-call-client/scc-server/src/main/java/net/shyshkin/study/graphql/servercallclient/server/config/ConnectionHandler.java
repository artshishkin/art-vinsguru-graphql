package net.shyshkin.study.graphql.servercallclient.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.server.service.ClientService;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionHandler {

    private final ClientService clientService;

    @ConnectMapping("movie-app-client")
    Mono<Void> handleConnection(RSocketRequester requester, @Payload UUID clientId) {
        return requester.rsocket()
                .onClose() // (1)
                .log()
                .doFirst(() -> {
                    log.debug("Client: {} CONNECTED.", clientId);
                    clientService.addClient(clientId, requester); // (2)
                })
                .doOnError(error -> {
                    log.warn("Channel to client {} CLOSED", clientId); // (3)
                })
                .doFinally(consumer -> {
                    clientService.removeClient(clientId, requester);
                    log.debug("Client {} DISCONNECTED", clientId); // (4)
                });
    }

}
