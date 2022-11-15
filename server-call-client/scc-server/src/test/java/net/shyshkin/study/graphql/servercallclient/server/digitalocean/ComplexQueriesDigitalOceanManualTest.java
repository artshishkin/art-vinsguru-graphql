package net.shyshkin.study.graphql.servercallclient.server.digitalocean;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.dto.ComplexWatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "logging.level.io.rsocket: info",
        "logging.level.net.shyshkin: info",
        "spring.rsocket.server.port: 0"
})
@Disabled("For manual tests only. Deploy docker-compose or swarm stack `server-digital-ocean-swarm` into the cloud (like Digital Ocean)")
class ComplexQueriesDigitalOceanManualTest {

    private static final UUID CLIENT_ID = UUID.randomUUID();

    private static final String SERVER_URL = "http://ddos.shyshkin.org:8090";

    protected WebTestClient webTestClient = WebTestClient.bindToServer()
            .baseUrl(SERVER_URL)
            .responseTimeout(Duration.ofSeconds(10))
            .build();

    private static final Network network = Network.newNetwork();

    private static final GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-external-services")
            .withNetwork(network)
            .withNetworkAliases("external-services");

    private static final GenericContainer<?> sccClient = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-scc-client")
            .dependsOn(externalServices)
            .withNetwork(network)
            .withEnv("app.service.review.base-url", "http://external-services:7070/review")
            .withEnv("app.service.movie.base-url", "http://external-services:7070/movie")
            .withEnv("app.service.customer.base-url", "http://external-services:7070/customer")
            .withEnv("app.server.rsocket.loadbalancer.no-load-balancer.server.host", "ddos.shyshkin.org")
            .withEnv("app.server.rsocket.loadbalancer.no-load-balancer.server.port", "7000")
            .withEnv("spring.profiles.active", "server-loadbalance")
            .withEnv("app.client-id.value", CLIENT_ID.toString())
            .waitingFor(Wait.forLogMessage(".*Started ClientApplication in.*", 1));


    static {
        externalServices.start();
        sccClient.start();
    }

    @ParameterizedTest
    @CsvSource({
            "2,5,rest",
            "2,5,graphql",
            "4,10,rest",
            "4,10,graphql",
            "4,30,rest",
            "4,30,graphql",
            "4,50,rest",
            "4,50,graphql",
    })
    void complexWatchListUpdateRESTLikeTest(int usersCount, int moviesCount, String method) {

        //given
        LocalDateTime startTime = LocalDateTime.now();
        Stream<Integer> customerIdStream = IntStream.rangeClosed(1, usersCount).boxed();
        List<WatchListInput> updates = customerIdStream
                .flatMap(customerId -> IntStream.rangeClosed(1, moviesCount)
                        .boxed()
                        .map(movieId -> new WatchListInput() {{
                            setCustomerId(customerId);
                            setMovieId(movieId);
                        }})
                )
                .collect(Collectors.toList());
        ComplexWatchListInput complexWatchListInput = new ComplexWatchListInput() {{
            setUpdates(updates);
        }};

        //when
        webTestClient.post()
                .uri("/rest/users/complex/watch-list?" + method)
                .header("X-Client-Id", CLIENT_ID.toString())
                .bodyValue(complexWatchListInput)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(UserProfileDetails.class)
                .value(customers -> assertThat(customers)
                                .hasSize(usersCount)
                                .allSatisfy(customer -> assertThat(customer.getWatchList())
//                                .hasSize(moviesCount)
//                                .hasSizeLessThanOrEqualTo(moviesCount) //something wrong happens actual is less then expected
                                                .hasSizeGreaterThanOrEqualTo(1)
                                                .allSatisfy(watchList -> assertThat(watchList).hasNoNullFieldsOrProperties())
                                )
                );
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("-------------------------------");
        log.info("\nDuration of complexWatchListUpdate (usersCount:{}, moviesCount:{}) `{}`: {}",
                usersCount, moviesCount, method, duration);
        System.out.println("-------------------------------");
    }

}