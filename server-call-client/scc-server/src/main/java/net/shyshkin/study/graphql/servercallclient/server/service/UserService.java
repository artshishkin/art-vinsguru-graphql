package net.shyshkin.study.graphql.servercallclient.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.common.dto.CustomerInput;
import net.shyshkin.study.graphql.servercallclient.common.dto.Status;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.client.CustomRSocketGraphQlClientBuilder;
import net.shyshkin.study.graphql.servercallclient.server.dto.DetailsType;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import net.shyshkin.study.graphql.servercallclient.server.dto.WatchList;
import org.springframework.graphql.client.RSocketGraphQlClient;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RSocketRequesterManager rSocketRequesterManager;

    public Mono<UserProfileDetails> getUserProfile(UUID requesterId, Integer userId, DetailsType detailsType) {
        String operationName = (detailsType == DetailsType.FULL) ? "getUserProfileFull" : "getUserProfileCut";
        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);
        return Mono.justOrEmpty(requesterOptional)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
                        .documentName("queries")
                        .operationName(operationName)
                        .variable("userId", userId)
                        .retrieve("userProfile")
                        .toEntity(UserProfileDetails.class)
                );
    }

    public Mono<UserProfileDetails> updateUserProfile(UUID requesterId, CustomerInput customerInput, DetailsType detailsType) {
        String operationName = (detailsType == DetailsType.FULL) ? "updateUserProfileFull" : "updateUserProfileCut";
        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);
        return Mono.justOrEmpty(requesterOptional)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
                        .documentName("mutations")
                        .operationName(operationName)
                        .variable("customerInput", customerInput)
                        .retrieve("result")
                        .toEntity(UserProfileDetails.class)
                );
    }

    public Mono<WatchList> addMovieToUserWatchList(UUID requesterId, WatchListInput watchListInput, DetailsType detailsType) {
        String operationName = (detailsType == DetailsType.FULL) ? "addMovieToUserWatchListFull" : "addMovieToUserWatchListSimple";
        Optional<RSocketRequester> requesterOptional = rSocketRequesterManager.getRequester(requesterId);
        return Mono.justOrEmpty(requesterOptional)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build())
                .flatMap(client -> client
                        .documentName("mutations")
                        .operationName(operationName)
                        .variable("watchListInput", watchListInput)
                        .retrieve("result")
                        .toEntity(WatchList.class)
                );
    }

    public Flux<UserProfileDetails> complexWatchListUpdateRESTLike(UUID requesterId, List<WatchListInput> watchListUpdates) {

        Flux<WatchListInput> watchListInputFlux = Flux.fromIterable(watchListUpdates);

        Optional<RSocketGraphQlClient> rSocketGraphQlClientOptional = rSocketRequesterManager
                .getRequester(requesterId)
                .map(requester -> new CustomRSocketGraphQlClientBuilder(requester).build());
        if (rSocketGraphQlClientOptional.isEmpty()) return Flux.empty();

        RSocketGraphQlClient rSocketGraphQlClient = rSocketGraphQlClientOptional.get();

        return watchListInputFlux
                .flatMap(watchListInput -> rSocketGraphQlClient
                        .documentName("mutations")
                        .operationName("addMovieToUserWatchListSimple")
                        .variable("watchListInput", watchListInput)
                        .retrieve("result")
                        .toEntity(WatchList.class)
                        .filter(watchList -> watchList.getStatus() == Status.SUCCESS)
                        .map(watchList -> watchListInput.getCustomerId())
                )
                .distinct()
                .flatMap(userId -> rSocketGraphQlClient
                        .documentName("queries")
                        .operationName("getUserProfileWatch")
                        .variable("userId", userId)
                        .retrieve("result")
                        .toEntity(UserProfileDetails.class)
                );
    }

    public Flux<UserProfileDetails> complexWatchListUpdateGraphQLLike(UUID requesterId, List<WatchListInput> watchListUpdates) {

        var rSocketRequesterOptional = rSocketRequesterManager
                .getRequester(requesterId);
        if (rSocketRequesterOptional.isEmpty()) return Flux.empty();
        RSocketRequester requester = rSocketRequesterOptional.get();
        RSocketGraphQlClient rSocketGraphQlClient = new CustomRSocketGraphQlClientBuilder(requester).build();

        Flux<WatchListInput> watchListInputFlux = Flux.fromIterable(watchListUpdates);

        Flux<Integer> customerIdFlux = watchListInputFlux
                .map(WatchListInput::getCustomerId)
                .distinct();

        return watchListInputFlux
                .distinct()
                .map(this::watchListInputToMutation)
                .collect(Collectors.joining("", "mutation {\n", "}"))
                .doOnNext(mutation -> log.debug("Final mutation: {}", mutation))
                .flatMap(mutation -> rSocketGraphQlClient
                        .document(mutation)
                        .retrieve("")
                        .toEntity(JsonNode.class)
                        .doOnNext(response -> log.debug("Response: {}", response))
                )
                .thenMany(customerIdFlux)
                .map(this::queryUserDetails)
                .collect(Collectors.joining("", "query{\n", "}"))
                .doOnNext(query -> log.debug("Final query: {}", query))
                .flatMapMany(query -> rSocketGraphQlClient
                        .document(query)
                        .execute()
                        .flatMapMany(response -> customerIdFlux
                                .map(customerId -> String.format("customer%02d", customerId))
                                .map(field -> response.field(field).toEntity(UserProfileDetails.class))
                        )
                );
    }

    private String watchListInputToMutation(WatchListInput input) {
        Integer customerId = input.getCustomerId();
        Integer movieId = input.getMovieId();
        return String.format("c%02dm%03d: addToWatchList(input: {customerId:%d,movieId:%d}){status}\n",
                customerId, movieId, customerId, movieId);
    }

    private String queryUserDetails(Integer customerId) {
        return String.format("customer%02d: userProfile(id: %d){\n" +
                        "        id\n" +
                        "        name\n" +
                        "        favoriteGenre\n" +
                        "        watchList {\n" +
                        "            id\n" +
                        "            title\n" +
                        "            genre\n" +
                        "            rating\n" +
                        "            releaseYear\n" +
                        "        }\n" +
                        "    }\n",
                customerId, customerId);
    }

}
