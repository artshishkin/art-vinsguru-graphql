package net.shyshkin.study.graphql.servercallclient.server.service;

import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
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
class MovieServiceMockTest {

    @Autowired
    MovieService movieService;

    @MockBean
    RSocketRequesterManager rSocketRequesterManager;

    @Mock
    RSocketRequester rSocketRequester;

    @Mock
    RSocketRequester.RequestSpec requestSpec;

    @Mock
    RSocketRequester.RetrieveSpec retrieveSpec;

    @Captor
    ArgumentCaptor<Map<String, Object>> requestDataCaptor;

    @Test
    void getMovieDetailsCutTest() {

        //given
        UUID requesterId = UUID.randomUUID();
        Integer movieId = 3;
        given(rSocketRequesterManager.getRequester(any())).willReturn(Optional.of(rSocketRequester));
        given(rSocketRequester.route(any())).willReturn(requestSpec);
        given(requestSpec.data(any())).willReturn(retrieveSpec);

        Movie expectedMovie = new Movie() {{
            setId(3);
            setTitle("Titanic");
            setReleaseYear(1997);
            setGenre(Genre.DRAMA);
            setRating(7.9);
        }};
        Map<String, Map<String, Object>> responseData = Map.of(
                "movieDetails",
                Map.of(
                        "id", 3,
                        "title", "Titanic",
                        "releaseYear", 1997,
                        "genre", "DRAMA",
                        "rating", 7.9
                )
        );
        given(retrieveSpec.retrieveMono(any(ParameterizedTypeReference.class))).willReturn(Mono.just(Map.of("data", responseData)));

        //when
        movieService.getMovieDetailsCut(requesterId, movieId)

                //then
                .as(StepVerifier::create)
                .expectNext(expectedMovie)
                .verifyComplete();

        then(rSocketRequesterManager).should().getRequester(eq(requesterId));
        then(rSocketRequester).should().route(eq("graphql"));
        then(requestSpec).should().data(requestDataCaptor.capture());
        Map<String, Object> requestData = requestDataCaptor.getValue();
        assertThat(requestData).containsKeys("query", "operationName", "variables");
        assertThat(requestData.get("query"))
                .satisfies(query -> assertThat((String) query)
                        .contains("query", "getMovieDetailsCut"));
        assertThat(requestData.get("operationName"))
                .satisfies(operationName -> assertThat((String) operationName).isEqualTo("getMovieDetailsCut"));
        assertThat(requestData.get("variables"))
                .satisfies(variables -> assertThat((Map<String, Object>) variables)
                        .satisfies(vars -> assertThat(vars.get("movieId")).isEqualTo(movieId)));
    }

}