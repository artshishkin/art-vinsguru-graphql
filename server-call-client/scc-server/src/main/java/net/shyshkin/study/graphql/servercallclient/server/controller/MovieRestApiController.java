package net.shyshkin.study.graphql.servercallclient.server.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.server.service.PingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("rest")
@RequiredArgsConstructor
public class MovieRestApiController {

    private final PingService pingService;

    @GetMapping("{requesterId}/ping")
    Mono<String> ping(@PathVariable UUID requesterId) {
        return pingService.ping(requesterId);
    }

}
