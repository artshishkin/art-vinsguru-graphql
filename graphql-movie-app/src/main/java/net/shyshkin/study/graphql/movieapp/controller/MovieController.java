package net.shyshkin.study.graphql.movieapp.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.movieapp.client.MovieClient;
import net.shyshkin.study.graphql.movieapp.dto.Customer;
import net.shyshkin.study.graphql.movieapp.dto.Movie;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieClient movieClient;

    @SchemaMapping(typeName = "UserProfile", field = "watchList")
    public Flux<Movie> getUserWatchListMovies(Customer customer) {
        return movieClient.getMoviesByIds(customer.getWatchList());
    }

    @SchemaMapping(typeName = "UserProfile", field = "recommended")
    public Flux<Movie> getRecommendedMovies(Customer customer) {
        return movieClient.getMovieRecommendationByGenre(customer.getFavoriteGenre());
    }

}
