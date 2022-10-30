package net.shyshkin.study.graphql.movieapp;

import net.shyshkin.study.graphql.movieapp.client.CustomerClient;
import net.shyshkin.study.graphql.movieapp.client.MovieClient;
import net.shyshkin.study.graphql.movieapp.client.ReviewClient;
import net.shyshkin.study.graphql.movieapp.dto.Genre;
import net.shyshkin.study.graphql.movieapp.dto.Movie;
import net.shyshkin.study.graphql.movieapp.dto.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@AutoConfigureHttpGraphQlTester
class GraphqlMovieApplicationTests extends BaseTest {

    @Autowired
    GraphQlTester graphQlTester;

    @SpyBean
    CustomerClient customerClient;

    @SpyBean
    MovieClient movieClient;

    @SpyBean
    ReviewClient reviewClient;

    @Test
    void contextLoads() {
    }

    @Test
    void getUserProfileById_absent() {
        //given
        Integer userId = 1000;

        //when
        graphQlTester.documentName("queries")
                .operationName("getUserProfileCut")
                .variable("userId", userId)
                .execute()

                //then
                .path("userProfile").valueIsNull();

        then(customerClient).should().getCustomerById(eq(userId));
        then(movieClient).shouldHaveNoInteractions();
        then(reviewClient).shouldHaveNoInteractions();
    }

    @Test
    void getUserProfileByIdCut_present() {
        //given
        Integer userId = 1;

        //when
        graphQlTester.documentName("queries")
                .operationName("getUserProfileCut")
                .variable("userId", userId)
                .execute()

                //then
                .path("userProfile").hasValue()
                .path("userProfile.id").entity(Integer.class).isEqualTo(userId)
                .path("userProfile.name").entity(String.class).satisfies(name -> assertThat(name).isNotEmpty())
                .path("userProfile.favoriteGenre").hasValue()
                .path("userProfile.watchList").pathDoesNotExist()
                .path("userProfile.recommended").pathDoesNotExist();

        then(customerClient).should().getCustomerById(eq(userId));
        then(movieClient).shouldHaveNoInteractions();
        then(reviewClient).shouldHaveNoInteractions();
    }

    @Test
    void getUserProfileByIdFull_present() {
        //given
        Integer userId = 1;

        //when
        GraphQlTester.Response response = graphQlTester.documentName("queries")
                .operationName("getUserProfileFull")
                .variable("userId", userId)
                .execute();

        //then
        response.path("userProfile").hasValue()
                .path("userProfile.id").entity(Integer.class).isEqualTo(userId)
                .path("userProfile.name").entity(String.class).satisfies(name -> assertThat(name).isNotEmpty())
                .path("userProfile.favoriteGenre").hasValue();
        response.path("userProfile.watchList")
                .hasValue()
                .entityList(Movie.class)
                .satisfies(movies -> assertThat(movies)
                        .allSatisfy(m -> assertThat(m).hasNoNullFieldsOrProperties()));
        response.path("userProfile.recommended")
                .hasValue()
                .entityList(Movie.class)
                .hasSizeGreaterThan(1)
                .satisfies(movies -> assertThat(movies)
                        .allSatisfy(m -> assertThat(m).hasNoNullFieldsOrProperties()));

        then(customerClient).should().getCustomerById(eq(userId));
        then(movieClient).should().getMoviesByIds(any());
        then(movieClient).should().getMovieRecommendationByGenre(any());
        then(reviewClient).shouldHaveNoInteractions();
    }

    @Test
    void getMovieDetails_absent() {
        //given
        Integer movieId = 10000;

        //when
        graphQlTester.documentName("queries")
                .operationName("getMovieDetailsCut")
                .variable("movieId", movieId)
                .execute()

                //then
                .path("movieDetails").valueIsNull();

        then(customerClient).shouldHaveNoInteractions();
        then(movieClient).should().getMoviesByIds(any());
        then(reviewClient).shouldHaveNoInteractions();
    }

    @Test
    void getMovieDetailsCut_present() {
        //given
        Integer movieId = 1;

        //when
        graphQlTester.documentName("queries")
                .operationName("getMovieDetailsCut")
                .variable("movieId", movieId)
                .execute()

                //then
                .path("movieDetails").hasValue()
                .entity(Movie.class)
                .satisfies(movie -> assertThat(movie).hasNoNullFieldsOrProperties());

        then(customerClient).shouldHaveNoInteractions();
        then(movieClient).should().getMoviesByIds(any());
        then(reviewClient).shouldHaveNoInteractions();
    }

    @Test
    void getMovieDetailsFull_present() {
        //given
        Integer movieId = 1;

        //when
        GraphQlTester.Response response = graphQlTester.documentName("queries")
                .operationName("getMovieDetailsFull")
                .variable("movieId", movieId)
                .execute();

        //then
        response.path("movieDetails").hasValue()
                .entity(Movie.class)
                .satisfies(movie -> assertThat(movie).hasNoNullFieldsOrProperties());
        response.path("movieDetails.reviews")
                .hasValue()
                .entityList(Review.class)
                .satisfies(reviews -> assertThat(reviews)
                        .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties()));

        then(customerClient).shouldHaveNoInteractions();
        then(movieClient).should().getMoviesByIds(any());
        then(reviewClient).should().reviews(eq(movieId));
    }

    @Test
    void getMoviesByGenreTest() {
        //given
        Genre genre = Genre.ACTION;

        //when
        GraphQlTester.Response response = graphQlTester.documentName("queries")
                .operationName("getMoviesByGenre")
                .variable("genre", genre)
                .execute();

        //then
        response.path("moviesByGenre").hasValue()
                .entityList(Movie.class)
                .satisfies(movies -> assertThat(movies)
                        .allSatisfy(movie -> assertThat(movie).hasNoNullFieldsOrProperties()));
        then(customerClient).shouldHaveNoInteractions();
        then(movieClient).should().getMovieRecommendationByGenre(eq(genre));
        then(reviewClient).shouldHaveNoInteractions();
    }

}