package net.shyshkin.study.graphql.movieapp.client;

import net.shyshkin.study.graphql.movieapp.dto.Genre;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Disabled("Only for manual testing. Run `external-services` first: `docker-compose up`")
class MovieClientManualTest {

    @Autowired
    MovieClient movieClient;

    @Test
    @DisplayName("Requesting movies by ids (PRESENT ids) should return correct movies")
    void getMoviesByIds_presentMoviesTest() {

        //given
        List<Integer> movieIds = List.of(1, 2, 3);

        //when
        movieClient.getMoviesByIds(movieIds)

                //then
                .as(StepVerifier::create)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(r -> true, movie -> assertAll(
                        () -> assertThat(movie).hasNoNullFieldsOrProperties(),
                        () -> assertThat(movie.getId()).isIn(movieIds))
                )
                .consumeRecordedWith(movies -> assertThat(movies).hasSize(3))
                .verifyComplete();
    }

    @Test
    @DisplayName("Requesting movies by ids (ABSENT ids) should return Empty result")
    void getMoviesByIds_absentMoviesTest() {

        //given
        List<Integer> movieIds = List.of(10000, 20000);

        //when
        movieClient.getMoviesByIds(movieIds)

                //then
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    @DisplayName("Requesting movies by ids (ABSENT ids) should return Empty result")
    void getMoviesByIds_emptyListIdsTest() {

        //given
        List<Integer> movieIds = List.of();

        //when
        movieClient.getMoviesByIds(movieIds)

                //then
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    @DisplayName("Requesting movie recommendations by genre (PRESENT) should return correct movies")
    void getMovieRecommendationByGenre_presentGenreTest() {

        //given
        Genre genre = Genre.ACTION;

        //when
        movieClient.getMovieRecommendationByGenre(genre)

                //then
                .as(StepVerifier::create)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(r -> true, movie -> assertAll(
                        () -> assertThat(movie).hasNoNullFieldsOrProperties(),
                        () -> assertThat(movie.getGenre()).isEqualTo(genre))
                )
                .consumeRecordedWith(movies -> assertThat(movies).isNotEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("Requesting movie recommendations by genre (ABSENT) should return Error signal")
    void getMovieRecommendationByGenre_absentGenreTest() {

        //given
        Genre genre = Genre.ABSENT_FOR_TEST;

        //when
        movieClient.getMovieRecommendationByGenre(genre)

                //then
                .as(StepVerifier::create)
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(WebClientResponseException.class)
                        .satisfies(ex -> assertThat((WebClientResponseException) ex)
                                .satisfies(respEx -> assertAll(
                                                () -> assertThat(respEx.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR),
                                                () -> assertThat(respEx.getResponseBodyAsString())
                                                        .contains("\"path\":", "\"/movie/ABSENT_FOR_TEST/recommended\"")
                                        )
                                )
                        )
                );
    }

}