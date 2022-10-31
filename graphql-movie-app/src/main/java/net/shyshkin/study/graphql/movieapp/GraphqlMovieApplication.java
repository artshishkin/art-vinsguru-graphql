package net.shyshkin.study.graphql.movieapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;

@SpringBootApplication(exclude = {ReactiveSecurityAutoConfiguration.class})
public class GraphqlMovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlMovieApplication.class, args);
    }

}
