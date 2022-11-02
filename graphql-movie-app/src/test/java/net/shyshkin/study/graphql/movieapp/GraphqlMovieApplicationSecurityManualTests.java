package net.shyshkin.study.graphql.movieapp;

import net.shyshkin.study.graphql.movieapp.client.CustomerClient;
import net.shyshkin.study.graphql.movieapp.client.MovieClient;
import net.shyshkin.study.graphql.movieapp.client.ReviewClient;
import net.shyshkin.study.graphql.movieapp.dto.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@ActiveProfiles("secure")
@AutoConfigureHttpGraphQlTester
@Disabled("Only for manual testing. Run `qraphql-oauth2` stack first: `docker-compose --env-file .env --profile=import-realm up -d`." +
        " Then get accessToken (through qraphql-oauth2/oauth-requests.http) and paste it into ACCESS_TOKEN")
class GraphqlMovieApplicationSecurityManualTests extends BaseTest {

    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1TFd2a1NTVjZPRmw3STMwT0p1UmZ1Z0tRQVlJXy1CZ0Y5cERJRGRuU3pjIn0.eyJleHAiOjE2NjcyOTczODAsImlhdCI6MTY2NzI5NzA4MSwiYXV0aF90aW1lIjowLCJqdGkiOiI2NjAzZTcwYi01YWYyLTQzZDMtYmQwZi0yODY1ODYxYjZlNTciLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgxODEvcmVhbG1zL2dyYXBocWwtbW92aWUtYXBwIiwiYXVkIjoiZ3JhcGhxbC1tb3ZpZS1hcHAtY2xpZW50Iiwic3ViIjoiZjIyMjAxODktMzY2Zi00ZWI5LWJmOWMtNTc3MWM3ZjAxNWE4IiwidHlwIjoiSUQiLCJhenAiOiJncmFwaHFsLW1vdmllLWFwcC1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiZjVjNzk3OWUtYzVmNS00ZjVkLThlNjgtMzVjMWEwM2RmOTA0IiwiYXRfaGFzaCI6Im02ZlBvNjk1TnE0N3FQNTZwXzBHbVEiLCJhY3IiOiIxIiwic2lkIjoiZjVjNzk3OWUtYzVmNS00ZjVkLThlNjgtMzVjMWEwM2RmOTA0IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiQXBwIFVzZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhcHAudXNlciIsImdpdmVuX25hbWUiOiJBcHAiLCJmYW1pbHlfbmFtZSI6IlVzZXIiLCJlbWFpbCI6ImFwcC51c2VyQGdtYWlsLmNvbSJ9.o826Gyc97A3WBkz0-YbhCL68zc6gZ0y07uVD9krsKOkv0QEtf1J7NIfY_nfheFYzO_H5mUxxifpAaKKO5yz62UNP8ykXsPP2gvEYg95VKKFMUpvwXTvDqXTitAfKUPPYwmzFGUukFLVTGUfJ4w2c5EwcLVhS9DC4zpcmPUzLy35snNEcoRiSJsVC03x-GQAz2qTpv75ckW7wwB_Qif3UQLY_TWpE9ATcQ3Gxac-VnFyAt-GUxhsq06Lm0lGc8XcROVu_xSF5p0tvYmTd07v5_wdJEw0YZsKSVA1CHWoqIp7Gq8UMziaf-y7fparULswEfZgEZQ0uC2_Rg0Pei0LEJw";

    @Autowired
    HttpGraphQlTester graphQlTester;

    GraphQlTester graphQlSecuredTester;

    @SpyBean
    CustomerClient customerClient;

    @SpyBean
    MovieClient movieClient;

    @SpyBean
    ReviewClient reviewClient;

    @BeforeEach
    void setUp() {
        graphQlSecuredTester = graphQlTester
                .mutate()
                .headers(httpHeaders -> httpHeaders.setBearerAuth(ACCESS_TOKEN))
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Nested
    class UnauthorizedTests {

        @Test
        void getUserProfileByIdCut_unauthorized() {
            //given
            Integer userId = 1;

            //when
            ThrowableAssert.ThrowingCallable execution = () -> {
                GraphQlTester.Response response = graphQlTester.documentName("queries")
                        .operationName("getUserProfileCut")
                        .variable("userId", userId)
                        .execute();
            };

            //then
            assertThatThrownBy(execution)
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("Status expected:<200 OK> but was:<401 UNAUTHORIZED>");

            then(customerClient).shouldHaveNoInteractions();
            then(movieClient).shouldHaveNoInteractions();
            then(reviewClient).shouldHaveNoInteractions();
        }
    }

    @Nested
    class QueryTests {

        @Test
        void getUserProfileById_absent() {
            //given
            Integer userId = 1000;

            //when
            graphQlSecuredTester.documentName("queries")
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
            graphQlSecuredTester.documentName("queries")
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
            GraphQlTester.Response response = graphQlSecuredTester.documentName("queries")
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
            graphQlSecuredTester.documentName("queries")
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
            graphQlSecuredTester.documentName("queries")
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
            GraphQlTester.Response response = graphQlSecuredTester.documentName("queries")
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
            GraphQlTester.Response response = graphQlSecuredTester.documentName("queries")
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
        void updateUserProfile_absent() {
            //given
            CustomerInput customerInput = new CustomerInput() {{
                setId(1000);
                setName("Abs");
                setFavoriteGenre(Genre.ADVENTURE);
            }};

            //when
            graphQlSecuredTester.documentName("mutations")
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
        void updateUserProfileCut_present() {
            //given
            CustomerInput customerInput = new CustomerInput() {{
                setId(1);
                setName("Arina");
                setFavoriteGenre(Genre.ADVENTURE);
            }};

            //when
            graphQlSecuredTester.documentName("mutations")
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
        void updateUserProfileFull_present() {
            //given
            CustomerInput customerInput = new CustomerInput() {{
                setId(2);
                setName("Mike");
                setFavoriteGenre(Genre.COMEDY);
            }};

            //when
            GraphQlTester.Response response = graphQlSecuredTester.documentName("mutations")
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
        void addMovieToUserWatchList_absentUser() {
            //given
            WatchListInput watchListInput = new WatchListInput() {{
                setCustomerId(1000);
                setMovieId(12);
            }};

            //when
            graphQlSecuredTester.documentName("mutations")
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
            graphQlSecuredTester.documentName("mutations")
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
            GraphQlTester.Response response = graphQlSecuredTester.documentName("mutations")
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
        void addMovieToUserWatchList_absentMovie() {
            //given
            int movieId = 12000;
            WatchListInput watchListInput = new WatchListInput() {{
                setCustomerId(3);
                setMovieId(movieId);
            }};

            //when
            GraphQlTester.Response response = graphQlSecuredTester.documentName("mutations")
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

    }

}