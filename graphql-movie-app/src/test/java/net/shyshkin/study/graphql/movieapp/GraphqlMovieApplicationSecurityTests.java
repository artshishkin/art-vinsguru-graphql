package net.shyshkin.study.graphql.movieapp;

import com.fasterxml.jackson.databind.JsonNode;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.movieapp.client.CustomerClient;
import net.shyshkin.study.graphql.movieapp.client.MovieClient;
import net.shyshkin.study.graphql.movieapp.client.ReviewClient;
import net.shyshkin.study.graphql.movieapp.dto.*;
import net.shyshkin.study.graphql.movieapp.util.VersionUtil;
import org.apache.commons.io.FilenameUtils;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@Slf4j
@ActiveProfiles("secure")
@AutoConfigureHttpGraphQlTester
class GraphqlMovieApplicationSecurityTests extends BaseTest {

    private static final String REALM_FILE_PATH = "../qraphql-oauth2/export/graphql-movie-app-realm.json";
    private static final String DEFAULT_REALM_IMPORT_FILES_LOCATION = "/opt/keycloak/data/import/";

    private static final String CLIENT_ID = "graphql-movie-app-client";
    private static final String CLIENT_SECRET = "NRNT4zeO5yTGBhnbb8eFqpRmAd2VbNAT";
    private static final String REALM_NAME = "graphql-movie-app";

    private static final String APP_USER_USERNAME = "app.user";
    private static final String APP_USER_PASSWORD = "123";
    private static final String APP_ADMIN_USERNAME = "app.admin";
    private static final String APP_ADMIN_PASSWORD = "234";
    private static final String APP_SUPER_USER_USERNAME = "app.superuser";
    private static final String APP_SUPER_USER_PASSWORD = "345";

    @Autowired
    HttpGraphQlTester graphQlTester;

    GraphQlTester graphQlSecuredTester;

    @SpyBean
    CustomerClient customerClient;

    @SpyBean
    MovieClient movieClient;

    @SpyBean
    ReviewClient reviewClient;

    @Container
    static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:" + VersionUtil.getVersion("KEYCLOAK_VERSION"))
            .withAdminUsername("admin")
            .withAdminPassword("Pa55w0rd")
            .withRealmImportFile("fake-realm.json") //fake insert to enable flag --import realm (+ withReuse() hashing workaround)
            .withCopyFileToContainer(
                    MountableFile.forHostPath(REALM_FILE_PATH),
                    DEFAULT_REALM_IMPORT_FILES_LOCATION + FilenameUtils.getName(REALM_FILE_PATH))
            .withReuse(false)
            .withStartupTimeout(Duration.ofMinutes(4));

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
    class SuperUserTests {

        @BeforeEach
        void setUp() {
            graphQlSecuredTester = graphQlTester
                    .mutate()
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(getJwtAccessToken(APP_SUPER_USER_USERNAME, APP_SUPER_USER_PASSWORD)))
                    .build();
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

    @Nested
    class AdminTests {

        @BeforeEach
        void setUp() {
            graphQlSecuredTester = graphQlTester
                    .mutate()
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(getJwtAccessToken(APP_ADMIN_USERNAME, APP_ADMIN_PASSWORD)))
                    .build();
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
                response.errors()
                        .satisfy(errs -> assertThat(errs).hasSize(1)
                                .satisfies(
                                        error -> assertAll(
                                                () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.FORBIDDEN),
                                                () -> assertThat(error.getMessage()).isEqualTo("Forbidden"),
                                                () -> assertThat(error.getPath()).isEqualTo("movieDetails.reviews")
                                        ),
                                        Index.atIndex(0)
                                )
                        );

                then(customerClient).shouldHaveNoInteractions();
                then(movieClient).should().getMoviesByIds(any());
                then(reviewClient).shouldHaveNoInteractions();
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

    @DynamicPropertySource
    static void appProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> String.format("%srealms/%s", keycloakContainer.getAuthServerUrl(), REALM_NAME)
        );
    }

    private final WebClient oauthServerWebClient = WebClient.builder()
            .baseUrl(keycloakContainer.getAuthServerUrl())
            .defaultHeaders(h -> h.setBasicAuth(CLIENT_ID, CLIENT_SECRET))
            .build();


    private String getJwtAccessToken(String username, String password) {

        Optional<String> fromCache = getFromCache(username, password);
        if (fromCache.isPresent())
            return fromCache.get();

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "password");

        map.add("username", username);
        map.add("password", password);
        map.add("scope", "openid profile");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
        var oAuthResponse = oauthServerWebClient.post()
                .uri("/realms/" + REALM_NAME + "/protocol/openid-connect/token")
                .body(BodyInserters.fromFormData(map))
                .exchangeToMono(clientResponse -> {
                    HttpStatus httpStatus = clientResponse.statusCode();
                    assertThat(httpStatus).isEqualTo(HttpStatus.OK);
                    return clientResponse.bodyToMono(JsonNode.class);
                })
                .block();

        //then
        log.debug("Response body from OAuth2.0 server: {}", oAuthResponse);
        assertThat(oAuthResponse).isNotNull();
        String accessToken = oAuthResponse.at("/access_token").asText();
        assertThat(accessToken).isNotEmpty();

        log.debug("JWT Access Token is {}", accessToken);
        saveToCache(username, password, accessToken);
        return accessToken;
    }

    private static final Map<String, String> cache = new HashMap<>();

    private Optional<String> getFromCache(String username, String password) {
        String key = username + ":" + password;
        return Optional.ofNullable(cache.get(key));
    }

    private void saveToCache(String username, String password, String accessToken) {
        String key = username + ":" + password;
        cache.put(key, accessToken);
    }

}