package net.shyshkin.study.graphql.movieapp.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!secure")
@EnableAutoConfiguration(exclude = {ReactiveSecurityAutoConfiguration.class})
public class TurnOffSecurityConfig {
}
