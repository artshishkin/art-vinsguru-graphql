package net.shyshkin.study.graphql.servercallclient.client;

import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.client.client.CustomerClient;
import net.shyshkin.study.graphql.servercallclient.client.client.MovieClient;
import net.shyshkin.study.graphql.servercallclient.client.client.ReviewClient;
import net.shyshkin.study.graphql.servercallclient.common.dto.CustomerInput;
import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.common.dto.Review;
import net.shyshkin.study.graphql.servercallclient.common.dto.Status;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.RSocketGraphQlTester;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "logging.level.io.rsocket: debug"
})
class ClientApplicationTests extends BaseTest {

    GraphQlTester graphQlTester;

    @LocalRSocketServerPort
    Integer rSocketPort;

    @SpyBean
    CustomerClient customerClient;

    @SpyBean
    MovieClient movieClient;

    @SpyBean
    ReviewClient reviewClient;

    @BeforeAll
    void beforeAll() {
        TcpClientTransport transport = TcpClientTransport.create(rSocketPort);
        graphQlTester = RSocketGraphQlTester.builder()
                .clientTransport(transport)
                //.dataMimeType(MediaType.APPLICATION_JSON)
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Nested
    class QueryTests {

        @Test
        void getUserProfileByIdWhenAbsent() {
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
        void getUserProfileByIdCutWhenPresent() {
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
        void getUserProfileByIdFullWhenPresent() {
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
        void getMovieDetailsWhenAbsent() {
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
        void getMovieDetailsCutWhenPresent() {
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
        void getMovieDetailsFullWhenPresent() {
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

    @Nested
    class MutationTests {

        @Test
        void updateUserProfileWhenAbsent() {
            //given
            CustomerInput customerInput = new CustomerInput() {{
                setId(1000);
                setName("Abs");
                setFavoriteGenre(Genre.ADVENTURE);
            }};

            //when
            graphQlTester.documentName("mutations")
                    .operationName("updateUserProfileCut")
                    .variable("customerInput", customerInput)
                    .execute()

                    //then
                    .path("result").valueIsNull();

            then(customerClient).should().updateCustomer(eq(customerInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).shouldHaveNoInteractions();
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        void updateUserProfileCutWhenPresent() {
            //given
            CustomerInput customerInput = new CustomerInput() {{
                setId(1);
                setName("Arina");
                setFavoriteGenre(Genre.ADVENTURE);
            }};

            //when
            graphQlTester.documentName("mutations")
                    .operationName("updateUserProfileCut")
                    .variable("customerInput", customerInput)
                    .execute()

                    //then
                    .path("result").hasValue()
                    .path("result.id").entity(Integer.class).isEqualTo(1)
                    .path("result.name").entity(String.class).isEqualTo("Arina")
                    .path("result.favoriteGenre").entity(Genre.class).isEqualTo(Genre.ADVENTURE)
                    .path("result.watchList").pathDoesNotExist()
                    .path("result.recommended").pathDoesNotExist();

            then(customerClient).should().updateCustomer(eq(customerInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).shouldHaveNoInteractions();
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        void updateUserProfileFullWhenPresent() {
            //given
            CustomerInput customerInput = new CustomerInput() {{
                setId(2);
                setName("Mike");
                setFavoriteGenre(Genre.COMEDY);
            }};

            //when
            GraphQlTester.Response response = graphQlTester.documentName("mutations")
                    .operationName("updateUserProfileFull")
                    .variable("customerInput", customerInput)
                    .execute();

            //then
            response.path("result").hasValue()
                    .path("result.id").entity(Integer.class).isEqualTo(2)
                    .path("result.name").entity(String.class).isEqualTo("Mike")
                    .path("result.favoriteGenre").entity(Genre.class).isEqualTo(Genre.COMEDY);
            response.path("result.watchList")
                    .hasValue()
                    .entityList(Movie.class)
                    .satisfies(movies -> assertThat(movies)
                            .allSatisfy(m -> assertThat(m).hasNoNullFieldsOrProperties()));
            response.path("result.recommended")
                    .hasValue()
                    .entityList(Movie.class)
                    .hasSizeGreaterThan(1)
                    .satisfies(movies -> assertThat(movies)
                            .allSatisfy(m -> assertThat(m).hasNoNullFieldsOrProperties()));

            then(customerClient).should().updateCustomer(eq(customerInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).should().getMoviesByIds(any());
            then(movieClient).should().getMovieRecommendationByGenre(any());
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Even if the customer does not exist adding movie to his list will be success???")
        void addMovieToUserWatchListWhenAbsentUser() {
            //given
            WatchListInput watchListInput = new WatchListInput() {{
                setCustomerId(1000);
                setMovieId(12);
            }};

            //when
            graphQlTester.documentName("mutations")
                    .operationName("addMovieToUserWatchListSimple")
                    .variable("watchListInput", watchListInput)
                    .execute()

                    //then
                    .path("result").hasValue()
                    .path("result.status").entity(Status.class).isEqualTo(Status.SUCCESS)
                    .path("result.watchList").pathDoesNotExist();

            then(customerClient).should().addMovieToCustomerWatchlist(eq(watchListInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).shouldHaveNoInteractions();
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("If customer exists and movie exists then adding movie to the customer's watchlist should be correct")
        void addMovieToUserWatchListSimple() {
            //given
            WatchListInput watchListInput = new WatchListInput() {{
                setCustomerId(3);
                setMovieId(12);
            }};

            //when
            graphQlTester.documentName("mutations")
                    .operationName("addMovieToUserWatchListSimple")
                    .variable("watchListInput", watchListInput)
                    .execute()

                    //then
                    .path("result").hasValue()
                    .path("result.status").entity(Status.class).isEqualTo(Status.SUCCESS)
                    .path("result.watchList").pathDoesNotExist();

            then(customerClient).should().addMovieToCustomerWatchlist(eq(watchListInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).shouldHaveNoInteractions();
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("When everything is OK then we should see movie in watchlist")
        void addMovieToUserWatchListFull() {
            //given
            int movieId = 124;
            WatchListInput watchListInput = new WatchListInput() {{
                setCustomerId(2);
                setMovieId(movieId);
            }};

            //when
            GraphQlTester.Response response = graphQlTester.documentName("mutations")
                    .operationName("addMovieToUserWatchListFull")
                    .variable("watchListInput", watchListInput)
                    .execute();

            //then
            response.path("result").hasValue();

            response.path("result.status").entity(Status.class).isEqualTo(Status.SUCCESS);

            response.path("result.watchList")
                    .hasValue()
                    .entityList(Movie.class)
                    .satisfies(movies -> assertThat(movies)
                            .allSatisfy(m -> assertThat(m).hasNoNullFieldsOrProperties())
                            .anySatisfy(m -> assertThat(m.getId()).isEqualTo(movieId))
                    );
            then(customerClient).should().addMovieToCustomerWatchlist(eq(watchListInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).should().getMoviesByIds(any());
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("Even if the movie does not exist adding it to watchlist will be success but movie will not return in response")
        void addMovieToUserWatchListWhenAbsentMovie() {
            //given
            int movieId = 12000;
            WatchListInput watchListInput = new WatchListInput() {{
                setCustomerId(3);
                setMovieId(movieId);
            }};

            //when
            GraphQlTester.Response response = graphQlTester.documentName("mutations")
                    .operationName("addMovieToUserWatchListFull")
                    .variable("watchListInput", watchListInput)
                    .execute();

            //then
            response.path("result").hasValue();

            response.path("result.status").entity(Status.class).isEqualTo(Status.SUCCESS);

            response.path("result.watchList")
                    .hasValue()
                    .entityList(Movie.class)
                    .satisfies(movies -> assertThat(movies)
                            .allSatisfy(m -> assertThat(m).hasNoNullFieldsOrProperties())
                            .allSatisfy(m -> assertThat(m.getId()).isNotEqualTo(movieId))
                    );

            then(customerClient).should().addMovieToCustomerWatchlist(eq(watchListInput));
            then(customerClient).shouldHaveNoMoreInteractions();
            then(movieClient).should().getMoviesByIds(any());
            then(reviewClient).shouldHaveNoInteractions();
        }

        @Test
        @Disabled("Can not work with SpyBean. Use separate AddToWatchListErrorTest with SpyBean")
        @DisplayName("When CustomerClient returns error then addingToWatchlist should have FAILURE status")
        void addMovieToUserWatchListWhenErrorInCustomerClient() {
            //given
            int movieId = 120;
            WatchListInput watchListInput = new WatchListInput();
            watchListInput.setCustomerId(3);
            watchListInput.setMovieId(movieId);

            given(customerClient.addMovieToCustomerWatchlist(eq(watchListInput)))
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
            then(movieClient).shouldHaveNoInteractions();
            then(reviewClient).shouldHaveNoInteractions();
        }

    }

}