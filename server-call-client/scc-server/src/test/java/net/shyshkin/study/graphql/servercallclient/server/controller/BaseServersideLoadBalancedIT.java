package net.shyshkin.study.graphql.servercallclient.server.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.server.service.RSocketGraphQlClientManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisabledIf(
        value = "hostTestcontainersInternalIsAbsent",
        disabledReason = "Please provide domain `host.testcontainers.internal` redirecting to 127.0.0.1 into `/etc/hosts` (or C:\\Windows\\System32\\drivers\\etc\\hosts in Windows)"
)
@TestPropertySource(properties = {
        "logging.level.io.rsocket: info"
})
@DirtiesContext
public abstract class BaseServersideLoadBalancedIT {

    protected static final UUID CLIENT_ID = UUID.randomUUID();

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    protected RSocketGraphQlClientManager rSocketGraphQlClientManager;

    protected static Network network = Network.newNetwork();

    protected static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-external-services")
            .withNetwork(network)
            .withNetworkAliases("external-services");

    protected static GenericContainer<?> sccClient = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-scc-client")
            .dependsOn(externalServices)
            .withNetwork(network)
            .withEnv("app.service.review.base-url", "http://external-services:7070/review")
            .withEnv("app.service.movie.base-url", "http://external-services:7070/movie")
            .withEnv("app.service.customer.base-url", "http://external-services:7070/customer")
            .withEnv("app.server.rsocket.loadbalancer.no-load-balancer.server.host", "nginx")
            .withEnv("app.server.rsocket.loadbalancer.no-load-balancer.server.port", "6999")
            .withEnv("spring.profiles.active", "server-loadbalance")
            .withEnv("app.client-id.value", CLIENT_ID.toString())
            .waitingFor(Wait.forLogMessage(".*Started ClientApplication in.*", 1));

    protected static GenericContainer<?> nginx = new GenericContainer<>(DockerImageName.parse("nginx").withTag("1.15-alpine"))
            .withAccessToHost(true)
            .withNetwork(network)
            .withNetworkAliases("nginx")
            .withCopyFileToContainer(MountableFile.forClasspathResource("nginx-test.conf"), "/etc/nginx/conf.d/nginx.conf")
            .withCommand("nginx -c /etc/nginx/conf.d/nginx.conf");

    static {
        externalServices.start();
        nginx.start();
        sccClient.start();
    }

    @BeforeAll
    static void beforeAll() {
        org.testcontainers.Testcontainers.exposeHostPorts(7003, 7001, 7002);
    }

    @BeforeEach
    void setUp() {
        waitForClientConnection();
    }

    private void waitForClientConnection() {
        log.info("Waiting for client: {} ({})", CLIENT_ID, this.getClass().getSimpleName());
        await()
                .pollInterval(Duration.ofMillis(100))
                .timeout(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    assertThat(rSocketGraphQlClientManager.getGraphQlClient(CLIENT_ID)).isPresent();
                });
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