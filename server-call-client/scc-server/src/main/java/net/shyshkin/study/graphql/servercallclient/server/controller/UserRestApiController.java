package net.shyshkin.study.graphql.servercallclient.server.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.server.dto.DetailsType;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import net.shyshkin.study.graphql.servercallclient.server.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

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

}
