package net.shyshkin.study.graphql.movieapp.client;

import net.shyshkin.study.graphql.movieapp.dto.Genre;
import net.shyshkin.study.graphql.movieapp.dto.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class MovieClient {

    private final WebClient webClient;

    public MovieClient(@Value("${app.service.movie.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }

    public Flux<Movie> getMoviesByIds(List<Integer> movieIds) {
        return (movieIds == null || movieIds.isEmpty()) ?
                Flux.empty() :
                getMoviesByIdsInternal(movieIds);
    }

    private Flux<Movie> getMoviesByIdsInternal(List<Integer> movieIds) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ids", movieIds).build())
                .retrieve()
                .bodyToFlux(Movie.class);
    }

    public Flux<Movie> getMovieRecommendationByGenre(Genre genre) {
        return webClient.get()
                .uri("/{genre}/recommended", genre)
                .retrieve()
                .bodyToFlux(Movie.class);
    }


}
