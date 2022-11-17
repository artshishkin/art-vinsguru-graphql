package net.shyshkin.study.graphql.servercallclient.server.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.server.dto.DetailsType;
import net.shyshkin.study.graphql.servercallclient.server.dto.MovieDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@Service
@RequiredArgsConstructor
public class MovieService {

    private final RSocketGraphQlClientManager rSocketGraphQlClientManager;

    public Mono<MovieDetails> getMovieDetails(UUID requesterId, Integer movieId, DetailsType detailsType) {
        String operationName = (detailsType == DetailsType.FULL) ? "getMovieDetailsFull" : "getMovieDetailsCut";
        return Mono.justOrEmpty(rSocketGraphQlClientManager.getGraphQlClient(requesterId))
                .flatMap(client -> client
                        .documentName("queries")
                        .operationName(operationName)
                        .variable("movieId", movieId)
                        .retrieve("movieDetails")
                        .toEntity(MovieDetails.class)
                );
    }

    public Flux<Movie> getMoviesByGenre(UUID requesterId, Genre genre) {
        return Mono.justOrEmpty(rSocketGraphQlClientManager.getGraphQlClient(requesterId))
                .switchIfEmpty(Mono.error(new RuntimeException("Client not found")))
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
