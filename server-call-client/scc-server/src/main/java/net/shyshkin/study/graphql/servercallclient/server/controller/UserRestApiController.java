package net.shyshkin.study.graphql.servercallclient.server.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.common.dto.CustomerInput;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.dto.ComplexWatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.dto.DetailsType;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import net.shyshkin.study.graphql.servercallclient.server.dto.WatchList;
import net.shyshkin.study.graphql.servercallclient.server.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SuppressFBWarnings("EI_EXPOSE_REP2")
@RestController
@RequestMapping("rest/users")
@RequiredArgsConstructor
public class UserRestApiController {

    private final UserService userService;

    @GetMapping("{userId}")
    Mono<UserProfileDetails> getUserProfile(@RequestHeader("X-Client-Id") UUID requesterId,
                                            @PathVariable Integer userId,
                                            @RequestParam(name = "detailsType", defaultValue = "CUT") DetailsType detailsType) {
        return userService.getUserProfile(requesterId, userId, detailsType);
    }

    @PutMapping("{userId}")
    Mono<UserProfileDetails> updateUserProfile(@RequestHeader("X-Client-Id") UUID requesterId,
                                               @PathVariable Integer userId,
                                               @RequestBody CustomerInput customerInput,
                                               @RequestParam(name = "detailsType", defaultValue = "CUT") DetailsType detailsType) {
        customerInput.setId(userId);
        return userService.updateUserProfile(requesterId, customerInput, detailsType);
    }

    @PostMapping("{userId}/watch-list")
    Mono<WatchList> addMovieToUserWatchList(@RequestHeader("X-Client-Id") UUID requesterId,
                                            @PathVariable Integer userId,
                                            @RequestBody WatchListInput watchListInput,
                                            @RequestParam(name = "detailsType", defaultValue = "CUT") DetailsType detailsType) {
        watchListInput.setCustomerId(userId);
        return userService.addMovieToUserWatchList(requesterId, watchListInput, detailsType);
    }

    @PostMapping("complex/watch-list")
    public Flux<UserProfileDetails> complexWatchListUpdateRESTLike(
            @RequestHeader("X-Client-Id") UUID requesterId,
            @RequestBody ComplexWatchListInput complexWatchListInput) {
        return userService.complexWatchListUpdateRESTLike(requesterId, complexWatchListInput.getUpdates());
    }

    @PostMapping(value = "complex/watch-list", params = "graphql")
    public Flux<UserProfileDetails> complexWatchListUpdateGraphQLLike(
            @RequestHeader("X-Client-Id") UUID requesterId,
            @RequestBody ComplexWatchListInput complexWatchListInput) {
        return userService.complexWatchListUpdateGraphQLLike(requesterId, complexWatchListInput.getUpdates());
    }

}
