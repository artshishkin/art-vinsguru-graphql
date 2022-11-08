package net.shyshkin.study.graphql.servercallclient.server.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.server.client.CustomRSocketGraphQlClientBuilder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final RSocketRequesterManager rSocketRequesterManager;

    public Mono<Movie> getMovieDetailsCut(UUID requesterId, Integer movieId) {
        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);
        return Mono.justOrEmpty(requesterOptional)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
                        .documentName("queries")
                        .operationName("getMovieDetailsCut")
                        .variable("movieId", movieId)
                        .retrieve("movieDetails")
                        .toEntity(Movie.class)
                );
    }

}
