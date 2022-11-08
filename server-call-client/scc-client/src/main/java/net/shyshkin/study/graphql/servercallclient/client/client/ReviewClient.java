package net.shyshkin.study.graphql.servercallclient.client.client;

import net.shyshkin.study.graphql.servercallclient.common.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class ReviewClient {

    private final WebClient webClient;

    public ReviewClient(@Value("${app.service.review.base-url}") String reviewServiceBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(reviewServiceBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                .build();
    }

    public Flux<Review> reviews(Integer movieId) {
        return webClient.get().uri("/{id}", movieId)
                .retrieve()
                .bodyToFlux(Review.class);
    }


}
