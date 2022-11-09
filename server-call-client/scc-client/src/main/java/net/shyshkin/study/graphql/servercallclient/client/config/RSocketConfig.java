package net.shyshkin.study.graphql.servercallclient.client.config;

import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.client.service.ClientIdService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Configuration
public class RSocketConfig {

    @Bean
    public RSocketRequester rsocketTcpRequester(RSocketRequester.Builder rsocketRequesterBuilder,
                                                RSocketMessageHandler handler,
                                                RSocketStrategies strategies,
                                                ClientIdService clientIdService,
                                                RSocketServerConfigData configData) {

        UUID clientId = clientIdService.getClientId();
        log.info("client ID {}", clientId);

        return rsocketRequesterBuilder
                .setupRoute(configData.getSetupRoute())
                .setupData(clientId)
                .rsocketStrategies(strategies)
                .rsocketConnector(connector -> connector
                        .reconnect(retryStrategy())
//                        .resume(resumeStrategy())
                        .acceptor(handler.responder()))
                .tcp(configData.getHost(), configData.getPort());
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
}
