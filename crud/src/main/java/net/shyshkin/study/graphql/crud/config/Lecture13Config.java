package net.shyshkin.study.graphql.crud.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Profile("lec13")
@Configuration
@ComponentScan(basePackages = "net.shyshkin.study.graphql.crud.lec13")
@EnableR2dbcRepositories(basePackages = "net.shyshkin.study.graphql.crud.lec13")
public class Lecture13Config {
}
