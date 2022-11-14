package net.shyshkin.study.graphql.servercallclient.server.loadbalanced.discovery;

import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.server.dto.MovieDetails;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {
        "spring.rsocket.server.port: 7006"
})
class MovieRestApiClientsideLBDiscoveryIT extends BaseClientsideLoadBalancedDiscoveryIT {

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

}