package net.shyshkin.study.graphql.movieapp.client;

import net.shyshkin.study.graphql.movieapp.BaseTest;
import net.shyshkin.study.graphql.movieapp.dto.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewClientTest extends BaseTest {

    @Autowired
    ReviewClient reviewClient;

    @Test
    @DisplayName("Requesting reviews of PRESENT movie should return correct reviews")
    void reviewsOfPresentMovieTest() {

        //given
        Integer movieId = 1;

        //when
        reviewClient.reviews(movieId)

                //then
                .as(StepVerifier::create)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(r -> true, review -> assertThat(review).hasNoNullFieldsOrProperties())
                .consumeRecordedWith(reviews -> assertThat(reviews).isNotEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("Requesting reviews of ABSENT movie should return Complete signal (empty list)")
    void reviewsOfAbsentMovieTest() {

        //given
        Integer movieId = 10_000;

        //when
        Flux<Review> reviews = reviewClient.reviews(movieId);

        //then
        StepVerifier.create(reviews)
                .verifyComplete();
    }
}