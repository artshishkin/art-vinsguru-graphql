package net.shyshkin.study.graphql.servercallclient.server.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.server.client.CustomRSocketGraphQlClientBuilder;
import net.shyshkin.study.graphql.servercallclient.server.dto.MovieDetails;
import net.shyshkin.study.graphql.servercallclient.server.dto.MovieDetailsType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final RSocketRequesterManager rSocketRequesterManager;

    public Mono<MovieDetails> getMovieDetails(UUID requesterId, Integer movieId, MovieDetailsType detailsType) {
        String operationName = (detailsType == MovieDetailsType.FULL) ? "getMovieDetailsFull" : "getMovieDetailsCut";
        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);
        return Mono.justOrEmpty(requesterOptional)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
                        .documentName("queries")
                        .operationName(operationName)
                        .variable("movieId", movieId)
                        .retrieve("movieDetails")
                        .toEntity(MovieDetails.class)
                );
    }

    public Flux<Movie> getMoviesByGenre(UUID requesterId, Genre genre) {
        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);
        return Mono.justOrEmpty(requesterOptional)
                .switchIfEmpty(Mono.error(new RuntimeException("Client not found")))
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
                        .documentName("queries")
                        .operationName("getMoviesByGenre")
                        .variable("genre", genre)
                        .retrieve("moviesByGenre")
                        .toEntityList(Movie.class)
                )
                .flatMapIterable(Function.identity());
    }

}
