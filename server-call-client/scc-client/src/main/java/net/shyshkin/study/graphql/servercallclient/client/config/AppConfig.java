package net.shyshkin.study.graphql.servercallclient.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.DefaultPropertiesPersister;

@Configuration
public class AppConfig {

    @Bean
    DefaultPropertiesPersister defaultPropertiesPersister() {
        return new DefaultPropertiesPersister();
    }

}
