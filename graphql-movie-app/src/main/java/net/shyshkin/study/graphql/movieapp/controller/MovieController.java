package net.shyshkin.study.graphql.movieapp.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.movieapp.client.MovieClient;
import net.shyshkin.study.graphql.movieapp.dto.Customer;
import net.shyshkin.study.graphql.movieapp.dto.Genre;
import net.shyshkin.study.graphql.movieapp.dto.Movie;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @QueryMapping("movieDetails")
    public Mono<Movie> getMovieDetails(@Argument Integer id) {
        return movieClient.getMoviesByIds(List.of(id))
                .next();
    }

    @QueryMapping
    public Flux<Movie> moviesByGenre(@Argument Genre genre) {
        return movieClient.getMovieRecommendationByGenre(genre);
    }

}
