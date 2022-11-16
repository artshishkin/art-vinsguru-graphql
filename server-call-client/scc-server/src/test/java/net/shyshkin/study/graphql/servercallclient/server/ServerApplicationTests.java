package net.shyshkin.study.graphql.servercallclient.server;

import net.shyshkin.study.graphql.servercallclient.server.service.RSocketGraphQlClientManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@SpringBootTest(properties = {
        "spring.rsocket.server.port: 0"
})
class ServerApplicationTests {

    private static final UUID CLIENT_ID = UUID.randomUUID();

    @LocalRSocketServerPort
    Integer rSocketServerPort;

    @Autowired
    RSocketRequester.Builder builder;

    RSocketRequester requester;

    @SpyBean
    RSocketGraphQlClientManager rSocketGraphQlClientManager;

    @Test
    void contextLoads() {
    }

    @Test
    void connectionTest() {

        //given
        requester = builder
                .setupData(CLIENT_ID)
                .setupRoute("movie-app-client")
                .tcp("localhost", rSocketServerPort);

        //when
        requester.route("hello")
                .data("Kate")
                .send()

                //then
                .as(StepVerifier::create)
                .verifyComplete();

        await()
                .timeout(2, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(50))
                .untilAsserted(() -> then(rSocketGraphQlClientManager).should().addRequester(eq(CLIENT_ID), any()));
    }
}