package net.shyshkin.study.graphql.movieapp.controller;

import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.movieapp.BaseTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.RSocketGraphQlTester;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "logging.level.io.rsocket: debug"
})
class PingControllerTest extends BaseTest {

    GraphQlTester graphQlTester;

    @LocalRSocketServerPort
    Integer rSocketPort;

    @BeforeAll
    void beforeAll() {
        TcpClientTransport transport = TcpClientTransport.create(rSocketPort);
        graphQlTester = RSocketGraphQlTester.builder()
                .clientTransport(transport)
                .build();
    }

    @Test
    void pingTest() {

        //when
        graphQlTester.documentName("ping")
                .execute()

                //then
                .path("ping").entity(String.class).isEqualTo("pong");
    }
}