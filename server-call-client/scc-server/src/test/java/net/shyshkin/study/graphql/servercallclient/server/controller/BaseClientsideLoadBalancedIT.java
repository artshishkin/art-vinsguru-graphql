package net.shyshkin.study.graphql.servercallclient.server.controller;

import net.shyshkin.study.graphql.servercallclient.server.service.RSocketRequesterManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
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
@TestPropertySource(properties = {
        "logging.level.io.rsocket: info"
})
public abstract class BaseClientsideLoadBalancedIT {

    protected static final UUID CLIENT_ID = UUID.randomUUID();

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected RSocketRequesterManager requesterManager;

    protected static Network network = Network.newNetwork();

    protected static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-external-services")
            .withNetwork(network)
            .withNetworkAliases("external-services");

    protected static GenericContainer<?> sccClient = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-scc-client")
            .dependsOn(externalServices)
            .withAccessToHost(true)
            .withNetwork(network)
            .withEnv("app.service.review.base-url", "http://external-services:7070/review")
            .withEnv("app.service.movie.base-url", "http://external-services:7070/movie")
            .withEnv("app.service.customer.base-url", "http://external-services:7070/customer")
            .withEnv("app.server.rsocket.loadbalancer.static-addresses-l-b.instances[0].host", "host.testcontainers.internal")
            .withEnv("app.server.rsocket.loadbalancer.static-addresses-l-b.instances[0].port", "7001")
            .withEnv("app.server.rsocket.loadbalancer.static-addresses-l-b.instances[1].host", "host.testcontainers.internal")
            .withEnv("app.server.rsocket.loadbalancer.static-addresses-l-b.instances[1].port", "7002")
            .withEnv("app.server.rsocket.loadbalancer.static-addresses-l-b.instances[2].host", "host.testcontainers.internal")
            .withEnv("app.server.rsocket.loadbalancer.static-addresses-l-b.instances[2].port", "7003")
            .withEnv("app.client-id.value", CLIENT_ID.toString())
            .waitingFor(Wait.forLogMessage(".*Started ClientApplication in.*", 1));

    static {
        externalServices.start();
        sccClient.start();
    }

    @BeforeAll
    static void beforeAll() {
        org.testcontainers.Testcontainers.exposeHostPorts(7001, 7002, 7003);
    }

    @BeforeEach
    void setUp() {
        waitForClientConnection();
    }

    private void waitForClientConnection() {
        await()
                .pollInterval(Duration.ofMillis(100))
                .timeout(Duration.ofSeconds(10))
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