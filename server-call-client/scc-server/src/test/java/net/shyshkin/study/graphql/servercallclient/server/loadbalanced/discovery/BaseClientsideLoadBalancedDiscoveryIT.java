package net.shyshkin.study.graphql.servercallclient.server.loadbalanced.discovery;

import net.shyshkin.study.graphql.servercallclient.server.service.RSocketGraphQlClientManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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
@ActiveProfiles("service-discovery")
public abstract class BaseClientsideLoadBalancedDiscoveryIT {

    protected static final UUID CLIENT_ID = UUID.randomUUID();

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected RSocketGraphQlClientManager rSocketGraphQlClientManager;

    protected static Network network = Network.newNetwork();

    protected static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-external-services")
            .withNetwork(network)
            .withNetworkAliases("external-services");

    protected static GenericContainer<?> discoveryService = new GenericContainer<>(DockerImageName.parse("consul").withTag("1.9.3"))
            .withNetwork(network)
            .withExposedPorts(8500)
            .withNetworkAliases("discovery-service");

    protected static GenericContainer<?> sccClient = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-scc-client")
            .dependsOn(externalServices, discoveryService)
            .withAccessToHost(true)
            .withNetwork(network)
            .withEnv("app.service.review.base-url", "http://external-services:7070/review")
            .withEnv("app.service.movie.base-url", "http://external-services:7070/movie")
            .withEnv("app.service.customer.base-url", "http://external-services:7070/customer")
            .withEnv("spring.profiles.active", "client-loadbalance-service-discovery")
            .withEnv("spring.cloud.consul.host", "discovery-service")
            .withEnv("logging.level.io.rsocket", "info")
            .withEnv("app.client-id.value", CLIENT_ID.toString())
            .waitingFor(Wait.forLogMessage(".*Started ClientApplication in.*", 1));

    static {
        externalServices.start();
        sccClient.start();
    }

    @BeforeAll
    static void beforeAll() {
        org.testcontainers.Testcontainers.exposeHostPorts(7004, 7005, 7006);
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
                        () -> assertThat(rSocketGraphQlClientManager.getGraphQlClient(CLIENT_ID)).isPresent()
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
            System.err.println("\n    -------------Skipping Test ------------ ");
            System.err.println("\n    Please provide domain `host.testcontainers.internal` redirecting to 127.0.0.1 into `/etc/hosts` (or C:\\\\Windows\\\\System32\\\\drivers\\\\etc\\\\hosts in Windows)\"\n");
        }
        return isAbsent;
    }

    @DynamicPropertySource
    static void discoveryServiceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.consul.host", () -> discoveryService.getHost());
        registry.add("spring.cloud.consul.port", () -> discoveryService.getMappedPort(8500));
    }

}