package net.shyshkin.study.graphql.servercallclient.server.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
class PingServiceMockTest {

    @Autowired
    PingService pingService;

    @MockBean
    RSocketRequesterManager rSocketRequesterManager;

    @Mock
    RSocketRequester rSocketRequester;

    @Mock
    RSocketRequester.RequestSpec requestSpec;

    @Mock
    RSocketRequester.RetrieveSpec retrieveSpec;

    @Test
    void pingTest() {
        //given
        UUID requesterId = UUID.randomUUID();
        given(rSocketRequesterManager.getRequester(any())).willReturn(Optional.of(rSocketRequester));
        given(rSocketRequester.route(any())).willReturn(requestSpec);
        given(requestSpec.data(any())).willReturn(retrieveSpec);
        given(retrieveSpec.retrieveMono(any(ParameterizedTypeReference.class))).willReturn(Mono.just(Map.of("data", Map.of("ping", "pong"))));

        //when
        pingService.ping(requesterId)

                //then
                .as(StepVerifier::create)
                .expectNext("pong")
                .verifyComplete();

        then(rSocketRequesterManager).should().getRequester(eq(requesterId));
        then(rSocketRequester).should().route(eq("graphql"));
        then(requestSpec).should().data(eq(Map.of("query", "query{\r\n" +
                "    ping\r\n" +
                "}"))
        );
    }
}