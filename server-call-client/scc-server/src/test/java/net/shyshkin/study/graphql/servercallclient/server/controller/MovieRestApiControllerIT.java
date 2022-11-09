package net.shyshkin.study.graphql.servercallclient.server.controller;

import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.server.dto.MovieDetails;
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
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Test
    void getMovieDetailsCutTest() {

        //given
        Integer movieId = 3;
        Movie expectedMovie = new Movie() {{
            setId(3);
            setTitle("Titanic");
            setReleaseYear(1997);
            setGenre(Genre.DRAMA);
            setRating(7.9);
        }};

        //when
        webTestClient.get()
                .uri("/rest/movies/{movieId}", movieId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .isEqualTo(expectedMovie);
    }

    @Test
    void getMovieDetailsFullTest() {

        //given
        Integer movieId = 5;

        //when
        webTestClient.get()
                .uri("/rest/movies/{movieId}?detailsType=FULL", movieId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(MovieDetails.class)
                .value(movie -> assertAll(
                                () -> assertThat(movie.getId()).isEqualTo(movieId),
                                () -> assertThat(movie.getTitle()).isEqualTo("Avengers: Infinity War"),
                                () -> assertThat(movie.getReleaseYear()).isEqualTo(2018),
                                () -> assertThat(movie.getGenre()).isEqualTo(Genre.ACTION),
                                () -> assertThat(movie.getRating()).isEqualTo(8.4),
                                () -> assertThat(movie.getReviews())
                                        .hasSizeGreaterThanOrEqualTo(1)
                                        .allSatisfy(review -> assertThat(review)
                                                .hasNoNullFieldsOrProperties())
                        )
                );
    }

    @Test
    void getMoviesByGenreTest() {

        //given
        Genre genre = Genre.ACTION;

        //when
        webTestClient.get()
                .uri("/rest/movies?genre=" + genre)
                .header("X-Client-Id", CLIENT_ID.toString())
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(Movie.class)
                .value(movies -> assertThat(movies)
                        .hasSizeGreaterThanOrEqualTo(1)
                        .allSatisfy(movie ->
                                assertAll(
                                        () -> assertThat(movie.getGenre()).isEqualTo(genre),
                                        () -> assertThat(movie).hasNoNullFieldsOrProperties()
                                )
                        )
                );
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