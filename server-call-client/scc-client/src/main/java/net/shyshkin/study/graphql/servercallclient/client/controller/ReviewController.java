package net.shyshkin.study.graphql.servercallclient.client.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.client.client.ReviewClient;
import net.shyshkin.study.graphql.servercallclient.client.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.client.dto.Review;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewClient reviewClient;

    @SchemaMapping(typeName = "MovieDetails", field = "reviews")
    public Flux<Review> getReviews(Movie movie) {
        return reviewClient.reviews(movie.getId());
    }

}
