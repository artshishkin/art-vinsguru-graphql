package net.shyshkin.study.graphql.client.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("lec16")
@Configuration
@ComponentScan(basePackages = "net.shyshkin.study.graphql.client.lec16")
public class Lecture16Config {
}
