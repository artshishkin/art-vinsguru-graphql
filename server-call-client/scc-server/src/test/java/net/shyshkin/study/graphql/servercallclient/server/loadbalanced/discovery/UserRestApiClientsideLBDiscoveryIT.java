package net.shyshkin.study.graphql.servercallclient.server.loadbalanced.discovery;

import net.shyshkin.study.graphql.servercallclient.common.dto.*;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import net.shyshkin.study.graphql.servercallclient.server.dto.WatchList;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {
        "spring.rsocket.server.port: 7005"
})
class UserRestApiClientsideLBDiscoveryIT extends BaseClientsideLoadBalancedDiscoveryIT {

    @Test
    void getUserProfileCutTest() {

        //given
        Integer userId = 1;
        UserProfile expectedUserProfile = new UserProfile() {{
            setId(userId);
            setName("sam");
            setFavoriteGenre(Genre.ACTION);
        }};

        //when
        webTestClient.get()
                .uri("/rest/users/{userId}", userId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(UserProfile.class)
                .isEqualTo(expectedUserProfile);
    }

    @Test
    void getUserProfileFullTest() {

        //given
        Integer userId = 1;

        //when
        webTestClient.get()
                .uri("/rest/users/{userId}?detailsType=FULL", userId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(UserProfileDetails.class)
                .value(u -> assertAll(
                                () -> assertThat(u).hasNoNullFieldsOrProperties(),
                                () -> assertThat(u.getId()).isEqualTo(userId),
                                () -> assertThat(u.getName()).isEqualTo("sam"),
                                () -> assertThat(u.getFavoriteGenre()).isEqualTo(Genre.ACTION),
                                () -> assertThat(u.getRecommended())
                                        .hasSizeGreaterThanOrEqualTo(2)
                                        .allSatisfy(movie -> assertThat(movie).hasNoNullFieldsOrProperties()),
                                () -> assertThat(u.getWatchList()).isNotNull()
                        )
                );
    }


    @Test
    void updateUserProfileCutTest() {

        //given
        Integer userId = 3;
        CustomerInput art = new CustomerInput() {{
            setId(userId);
            setName("Kate");
            setFavoriteGenre(Genre.DRAMA);
        }};

        //when
        webTestClient.put()
                .uri("/rest/users/{userId}", userId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .bodyValue(art)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(UserProfileDetails.class)
                .value(u -> assertAll(
                                () -> assertThat(u.getId()).isEqualTo(userId),
                                () -> assertThat(u.getName()).isEqualTo("Kate"),
                                () -> assertThat(u.getFavoriteGenre()).isEqualTo(Genre.DRAMA),
                                () -> assertThat(u.getRecommended()).isNull(),
                                () -> assertThat(u.getWatchList()).isNull()
                        )
                );
    }

    @Test
    void updateUserProfileFullTest() {

        //given
        Integer userId = 3;
        CustomerInput art = new CustomerInput() {{
            setId(userId);
            setName("Art");
            setFavoriteGenre(Genre.ADVENTURE);
        }};

        //when
        webTestClient.put()
                .uri("/rest/users/{userId}?detailsType=FULL", userId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .bodyValue(art)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(UserProfileDetails.class)
                .value(u -> assertAll(
                                () -> assertThat(u).hasNoNullFieldsOrProperties(),
                                () -> assertThat(u.getId()).isEqualTo(userId),
                                () -> assertThat(u.getName()).isEqualTo("Art"),
                                () -> assertThat(u.getFavoriteGenre()).isEqualTo(Genre.ADVENTURE),
                                () -> assertThat(u.getRecommended())
                                        .hasSizeGreaterThanOrEqualTo(2)
                                        .allSatisfy(movie -> assertThat(movie)
                                                .hasNoNullFieldsOrProperties()
                                                .hasFieldOrPropertyWithValue("genre", Genre.ADVENTURE)),
                                () -> assertThat(u.getWatchList()).isNotNull()
                        )
                );
    }

    @Test
    void addMovieToUserWatchListSimplifiedTest() {

        //given
        Integer userId = 3;
        Integer movieId = 55;
        WatchListInput watchListInput = new WatchListInput() {{
            setCustomerId(userId);
            setMovieId(movieId);
        }};

        //when
        webTestClient.post()
                .uri("/rest/users/{userId}/watch-list", userId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .bodyValue(watchListInput)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(WatchList.class)
                .value(response -> assertAll(
                                () -> assertThat(response.getStatus()).isEqualTo(Status.SUCCESS),
                                () -> assertThat(response.getWatchList()).isNull()
                        )
                );
    }

    @Test
    void addMovieToUserWatchListFullTest() {

        //given
        Integer userId = 3;
        Integer movieId = 5;
        WatchListInput watchListInput = new WatchListInput() {{
            setCustomerId(userId);
            setMovieId(movieId);
        }};

        //when
        webTestClient.post()
                .uri("/rest/users/{userId}/watch-list?detailsType=FULL", userId)
                .header("X-Client-Id", CLIENT_ID.toString())
                .bodyValue(watchListInput)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(WatchList.class)
                .value(response -> assertAll(
                                () -> assertThat(response).hasNoNullFieldsOrProperties(),
                                () -> assertThat(response.getStatus()).isEqualTo(Status.SUCCESS),
                                () -> assertThat(response.getWatchList())
                                        .hasSizeGreaterThanOrEqualTo(1)
                                        .anySatisfy(movie -> assertThat(movie)
                                                .hasNoNullFieldsOrProperties()
                                                .hasFieldOrPropertyWithValue("id", movieId))
                        )
                );
    }

}