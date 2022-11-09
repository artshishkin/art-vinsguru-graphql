package net.shyshkin.study.graphql.servercallclient.server.controller;

import net.shyshkin.study.graphql.servercallclient.server.service.RSocketRequesterManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisabledIf(
        value = "hostTestcontainersInternalIsAbsent",
        disabledReason = "Please provide domain `host.testcontainers.internal` redirecting to 127.0.0.1 into `/etc/hosts` (or C:\\Windows\\System32\\drivers\\etc\\hosts in Windows)"
)
class MovieRestApiControllerIT {

    private static final UUID CLIENT_ID = UUID.randomUUID();

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    RSocketRequesterManager requesterManager;

    static Network network = Network.newNetwork();

    @Container
    static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-external-services")
            .withNetwork(network)
            .withNetworkAliases("external-services");

    @Container
    static GenericContainer<?> sccClient = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-scc-client")
            .dependsOn(externalServices)
            .withAccessToHost(true)
            .withNetwork(network)
            .withEnv("app.service.review.base-url", "http://external-services:7070/review")
            .withEnv("app.service.movie.base-url", "http://external-services:7070/movie")
            .withEnv("app.service.customer.base-url", "http://external-services:7070/customer")
            .withEnv("app.server.rsocket.host", "host.testcontainers.internal")
            .withEnv("app.client-id.value", CLIENT_ID.toString())
            .waitingFor(Wait.forLogMessage(".*Started ClientApplication in.*", 1));

    @BeforeAll
    static void beforeAll() {
        org.testcontainers.Testcontainers.exposeHostPorts(7000);
    }

    @BeforeEach
    void setUp() {
        waitForClientConnection();
    }

    @Test
    void pingTest() {

        //when
        webTestClient.get()
                .uri("/rest/ping")
                .header("X-Client-Id", CLIENT_ID.toString())
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("pong");
    }

    private void waitForClientConnection() {
        await()
                .pollInterval(Duration.ofMillis(100))
                .timeout(Duration.ofSeconds(2))
                .untilAsserted(
                        () -> assertThat(requesterManager.getRequester(CLIENT_ID)).isPresent()
                );
    }

    static boolean hostTestcontainersInternalIsAbsent() {

        boolean isAbsent = true;
        try {
            InetAddress[] hosts = InetAddress.getAllByName("host.testcontainers.internal");
            isAbsent = hosts.length == 0;
        } catch (UnknownHostException ignored) {
        }

        if (isAbsent) {
            System.err.println("\n    -------------Skipping Test MovieRestApiControllerIT------------ ");
            System.err.println("\n    Please provide domain `host.testcontainers.internal` redirecting to 127.0.0.1 into `/etc/hosts` (or C:\\\\Windows\\\\System32\\\\drivers\\\\etc\\\\hosts in Windows)\"\n");
        }
        return isAbsent;
    }

}