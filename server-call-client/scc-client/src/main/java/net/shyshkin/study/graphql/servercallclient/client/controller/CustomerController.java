package net.shyshkin.study.graphql.servercallclient.client.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.servercallclient.client.client.CustomerClient;
import net.shyshkin.study.graphql.servercallclient.common.dto.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerClient customerClient;

    @QueryMapping("userProfile")
    public Mono<Customer> getUserProfile(@Argument Integer id) {
        return customerClient.getCustomerById(id);
    }

    @MutationMapping
    public Mono<Customer> setProfile(@Argument CustomerInput input) {
        return customerClient.updateCustomer(input);
    }

    @MutationMapping
    public Mono<WatchListResponse> addToWatchList(@Argument WatchListInput input) {
        return customerClient.addMovieToCustomerWatchlist(input)
                .collectList()
                .map(ids -> WatchListResponse.builder()
                        .status(Status.SUCCESS)
                        .watchList(ids)
                        .build())
                .onErrorReturn(WatchListResponse.builder()
                        .status(Status.FAILURE)
                        .watchList(List.of())
                        .build());
    }

}
