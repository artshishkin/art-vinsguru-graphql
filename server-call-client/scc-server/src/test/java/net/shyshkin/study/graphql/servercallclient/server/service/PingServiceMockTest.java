package net.shyshkin.study.graphql.servercallclient.server.service;

import net.shyshkin.study.graphql.servercallclient.server.client.CustomRSocketGraphQlClientBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.RSocketGraphQlClient;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
class PingServiceMockTest {

    @Autowired
    PingService pingService;

    @MockBean
    RSocketGraphQlClientManager rSocketGraphQlClientManager;

    @Mock
    RSocketRequester rSocketRequester;

    @Mock
    RSocketRequester.RequestSpec requestSpec;

    @Mock
    RSocketRequester.RetrieveSpec retrieveSpec;

    @Captor
    ArgumentCaptor<Map<String, String>> requestDataCaptor;

    @Test
    void pingTest() {
        //given
        UUID requesterId = UUID.randomUUID();
        RSocketGraphQlClient mockRSocketGraphQlClient = new CustomRSocketGraphQlClientBuilder(rSocketRequester).build();
        given(rSocketGraphQlClientManager.getGraphQlClient(any())).willReturn(Optional.of(mockRSocketGraphQlClient));
        given(rSocketRequester.route(any())).willReturn(requestSpec);
        given(requestSpec.data(any())).willReturn(retrieveSpec);
        given(retrieveSpec.retrieveMono(any(ParameterizedTypeReference.class))).willReturn(Mono.just(Map.of("data", Map.of("ping", "pong"))));

        //when
        pingService.ping(requesterId)

                //then
                .as(StepVerifier::create)
                .expectNext("pong")
                .verifyComplete();

        then(rSocketGraphQlClientManager).should().getGraphQlClient(eq(requesterId));
        then(rSocketRequester).should().route(eq("graphql"));
        then(requestSpec).should().data(requestDataCaptor.capture());
        Map<String, String> requestData = requestDataCaptor.getValue();
        assertThat(requestData).containsKey("query");
        assertThat(requestData.get("query"))
                .satisfies(query -> assertThat(query.replaceAll("\\s", ""))
                        .isEqualTo("query{ping}"));
    }
}