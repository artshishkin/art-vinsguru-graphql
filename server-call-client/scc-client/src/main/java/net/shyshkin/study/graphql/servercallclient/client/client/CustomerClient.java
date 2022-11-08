package net.shyshkin.study.graphql.servercallclient.client.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.common.dto.Customer;
import net.shyshkin.study.graphql.servercallclient.common.dto.CustomerInput;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomerClient {

    private final WebClient webClient;

    public CustomerClient(@Value("${app.service.customer.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<Customer> getCustomerById(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    public Mono<Customer> updateCustomer(CustomerInput customerInput) {
        return webClient.put()
                .uri("/{id}", customerInput.getId())
                .bodyValue(customerInput)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    public Flux<Integer> addMovieToCustomerWatchlist(WatchListInput input) {
        return webClient.post()
                .uri("/watchlist")
                .bodyValue(input)
                .retrieve()
                .bodyToFlux(Integer.class);
    }

}
