package net.shyshkin.study.graphql.servercallclient.client.config;

import io.rsocket.core.Resume;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.loadbalance.RoundRobinLoadbalanceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.client.service.ClientIdService;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RSocketConfig {

    private final RSocketServerConfigData configData;

    /*
    @Bean
    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies.builder()
                .encoders(encoders -> {
                    encoders.add(new Jackson2CborEncoder());
                    encoders.add(new Jackson2JsonEncoder());
                })
                .decoders(decoders -> {
                    decoders.add(new Jackson2CborDecoder());
                    decoders.add(new Jackson2JsonDecoder());
                })
                .build();
    }
    */

    @Bean
    @Profile({"client-loadbalance-service-discovery", "client-loadbalance-static-addresses"})
    public RSocketRequester clientLoadBalancedRsocketTcpRequester(RSocketRequester.Builder rsocketRequesterBuilder,
                                                                  RSocketMessageHandler handler,
                                                                  RSocketStrategies strategies,
                                                                  ClientIdService clientIdService,
                                                                  Publisher<List<LoadbalanceTarget>> targets) {

        UUID clientId = clientIdService.getClientId();
        log.info("client ID {}", clientId);

        var loadbalanceStrategy = new RoundRobinLoadbalanceStrategy();

        RSocketRequester requester = rsocketRequesterBuilder
                .setupRoute(configData.getSetupRoute())
                .setupData(clientId)
                .rsocketStrategies(strategies)
                .rsocketConnector(connector -> connector
                        .reconnect(retryStrategy())
                        //.resume(resumeStrategy())
                        .acceptor(handler.responder()))
                .transports(targets, loadbalanceStrategy);

        eagerReconnect(requester);

        return requester;
    }

    @Bean
    @Profile({"default", "server-loadbalance"})
    public RSocketRequester defaultRsocketTcpRequester(RSocketRequester.Builder rsocketRequesterBuilder,
                                                       RSocketMessageHandler handler,
                                                       RSocketStrategies strategies,
                                                       ClientIdService clientIdService) {

        UUID clientId = clientIdService.getClientId();
        log.info("client ID {}", clientId);

        RSocketServerConfigData.Loadbalancer.NoLoadBalancer.ServerAddress server = configData.getLoadbalancer().getNoLoadBalancer().getServer();
        RSocketRequester requester = rsocketRequesterBuilder
                .setupRoute(configData.getSetupRoute())
                .setupData(clientId)
                .rsocketStrategies(strategies)
                .rsocketConnector(connector -> connector
                        .reconnect(retryStrategy())
                        //.resume(resumeStrategy())
                        .acceptor(handler.responder()))
                .tcp(server.getHost(), server.getPort());

        eagerReconnect(requester);

        return requester;
    }

    private Resume resumeStrategy() {
        return new Resume()
                .retry(Retry
                        .fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
                        .doBeforeRetry(s -> log.debug("Resume session: retries in a row {}, total {}", s.totalRetriesInARow(), s.totalRetries())));
    }

    private Retry retryStrategy() {
        return Retry
                .fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(1))
                .doBeforeRetry(r -> log.debug("Retrying connection: retries in a row {}, total {}", r.totalRetriesInARow(), r.totalRetries()));
    }

    private void eagerReconnect(RSocketRequester requester) {
        requester
                .rsocketClient()
                .source()
                .doOnNext(rSocket -> log.debug("eager reconnect"))
                .delayElement(Duration.ofMillis(100))
                .flatMap(rsocket -> rsocket.onClose())
                .repeat()
                .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(1)))
                .subscribe();
    }

}
