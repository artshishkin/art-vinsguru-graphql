package net.shyshkin.study.graphql.movieapp;

import net.shyshkin.study.graphql.movieapp.client.CustomerClient;
import net.shyshkin.study.graphql.movieapp.client.MovieClient;
import net.shyshkin.study.graphql.movieapp.client.ReviewClient;
import net.shyshkin.study.graphql.movieapp.dto.Movie;
import net.shyshkin.study.graphql.movieapp.dto.Status;
import net.shyshkin.study.graphql.movieapp.dto.WatchListInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@AutoConfigureHttpGraphQlTester
class AddToWatchListErrorTest extends BaseTest {

    @Autowired
    GraphQlTester graphQlTester;

    @MockBean
    CustomerClient customerClient;

    @SpyBean
    MovieClient movieClient;

    @SpyBean
    ReviewClient reviewClient;

    @Test
    @DisplayName("When CustomerClient returns error then addingToWatchlist should have FAILURE status")
    void addMovieToUserWatchList_errorInCustomerClient() {
        //given
        int movieId = 120;
        WatchListInput watchListInput = new WatchListInput(){{
            setCustomerId(3);
            setMovieId(movieId);
        }};

        given(customerClient.addMovieToCustomerWatchlist(any(WatchListInput.class)))
                .willReturn(Flux.error(new RuntimeException("Some Weird Error")));

        //when
        GraphQlTester.Response response = graphQlTester.documentName("mutations")
                .operationName("addMovieToUserWatchListFull")
                .variable("watchListInput", watchListInput)
                .execute();

        //then
        response.path("result").hasValue();

        response.path("result.status").entity(Status.class).isEqualTo(Status.FAILURE);

        response.path("result.watchList")
                .hasValue()
                .entityList(Movie.class)
                .hasSize(0);

        then(customerClient).should().addMovieToCustomerWatchlist(eq(watchListInput));
        then(customerClient).shouldHaveNoMoreInteractions();
        then(movieClient).should().getMoviesByIds(eq(List.of()));
        then(reviewClient).shouldHaveNoInteractions();
    }

}

