package net.shyshkin.study.graphql.servercallclient.server.controller;

import net.shyshkin.study.graphql.servercallclient.common.dto.CustomerInput;
import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.UserProfile;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class UserRestApiControllerIT extends BaseControllerIT {

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


}