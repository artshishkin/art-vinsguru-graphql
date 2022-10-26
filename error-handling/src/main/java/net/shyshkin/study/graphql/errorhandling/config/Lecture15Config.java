package net.shyshkin.study.graphql.errorhandling.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Profile("lec15")
@Configuration
@ComponentScan(basePackages = "net.shyshkin.study.graphql.errorhandling.lec15")
@EnableR2dbcRepositories(basePackages = "net.shyshkin.study.graphql.errorhandling.lec15")
public class Lecture15Config {
}
