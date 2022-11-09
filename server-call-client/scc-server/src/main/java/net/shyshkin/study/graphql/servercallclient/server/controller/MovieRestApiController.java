package net.shyshkin.study.graphql.servercallclient.server.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.common.dto.Genre;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.server.dto.MovieDetailsType;
import net.shyshkin.study.graphql.servercallclient.server.service.MovieService;
import net.shyshkin.study.graphql.servercallclient.server.service.PingService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("rest")
@RequiredArgsConstructor
public class MovieRestApiController {

    private final PingService pingService;
    private final MovieService movieService;

    @GetMapping("ping")
    Mono<String> ping(@RequestHeader("X-Client-Id") UUID requesterId) {
        return pingService.ping(requesterId);
    }

    @GetMapping("movies/{movieId}")
    Mono<? extends Movie> getMovieDetails(@RequestHeader("X-Client-Id") UUID requesterId,
                                          @PathVariable Integer movieId,
                                          @RequestParam(name = "detailsType", defaultValue = "CUT") MovieDetailsType detailsType) {
        return movieService.getMovieDetails(requesterId, movieId, detailsType);
    }

    @GetMapping(value = "movies", params = "genre")
    Flux<Movie> getMovieDetails(@RequestHeader("X-Client-Id") UUID requesterId,
                                @RequestParam Genre genre) {
        return movieService.getMoviesByGenre(requesterId, genre);
    }

}
