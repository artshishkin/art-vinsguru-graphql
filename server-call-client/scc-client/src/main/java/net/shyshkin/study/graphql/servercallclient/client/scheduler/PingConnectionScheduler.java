package net.shyshkin.study.graphql.servercallclient.client.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Profile({"client-loadbalance-service-discovery", "client-loadbalance-static-addresses"})
public class PingConnectionScheduler {

    private final RSocketRequester requester;

    @Scheduled(fixedDelay = 1000)
    public void fireAndForgetServerCall() {
        log.debug("Sending `hello` message to connect to server periodically");
        requester.route("hello")
                .data("Art")
                .send()
                .subscribe();
    }

}
