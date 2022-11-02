package net.shyshkin.study.graphql.movieapp.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.movieapp.dto.Customer;
import net.shyshkin.study.graphql.movieapp.dto.CustomerInput;
import net.shyshkin.study.graphql.movieapp.dto.WatchListInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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

    @PreAuthorize("hasAuthority('ROLE_app_admin_role') or hasRole('app_super_user_role') or principal.getClaim('movie_app_user_id') == #id")
    public Mono<Customer> getCustomerById(Integer id) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> {
                    log.debug("Security context: {}", securityContext);
                })
                .then(
                        webClient.get()
                                .uri("/{id}", id)
                                .retrieve()
                                .bodyToMono(Customer.class)
                );
    }

    @PreAuthorize("hasAnyRole('app_admin_role','app_super_user_role') or principal.getClaim('movie_app_user_id') == #customerInput.id")
    public Mono<Customer> updateCustomer(CustomerInput customerInput) {
        return webClient.put()
                .uri("/{id}", customerInput.getId())
                .bodyValue(customerInput)
                .retrieve()
                .bodyToMono(Customer.class);
    }

    @PreAuthorize("hasAnyRole('app_admin_role','app_super_user_role') or principal.getClaim('movie_app_user_id') == #input.customerId")
    public Flux<Integer> addMovieToCustomerWatchlist(WatchListInput input) {
        return webClient.post()
                .uri("/watchlist")
                .bodyValue(input)
                .retrieve()
                .bodyToFlux(Integer.class);
    }

}
