package net.shyshkin.study.graphql.servercallclient.client.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Initializer {

    private final RSocketRequester requester;

    @EventListener
    public void initialFireAndForgetServerCall(ApplicationStartedEvent event) {
        log.debug("Sending `hello` message to connect to server");
        requester.route("hello")
                .data("Art")
                .send()
                .subscribe();
    }


}
