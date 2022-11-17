package net.shyshkin.study.graphql.servercallclient.server.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.rsocket.RSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.server.service.RSocketGraphQlClientManager;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Slf4j
@Controller
@RequiredArgsConstructor
public class ConnectionHandler {

    private final RSocketGraphQlClientManager rSocketGraphQlClientManager;

    @ConnectMapping("movie-app-client")
    Mono<Void> handleConnection(RSocketRequester requester, @Payload UUID requesterId) {
        return Mono.fromRunnable(() -> this.connectRequester(requesterId, requester));
    }

    private void connectRequester(UUID clientId, RSocketRequester requester) {
        Mono.justOrEmpty(requester)
                .mapNotNull(RSocketRequester::rsocket)
                .flatMap(RSocket::onClose)
                .log()
                .doFirst(() -> {
                    log.debug("Client: {} CONNECTED.", clientId);
                    rSocketGraphQlClientManager.addRequester(clientId, requester); // (2)
                })
                .doOnError(error -> {
                    log.error("Channel to client {} CLOSED", clientId, error); // (3)
                })
                .doFinally(consumer -> {
                    rSocketGraphQlClientManager.removeRequester(clientId, requester);
                    log.debug("Client {} DISCONNECTED", clientId); // (4)
                })
                .subscribe();
    }

}
