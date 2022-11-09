package net.shyshkin.study.graphql.servercallclient.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.service")
public class ExternalServicesConfigData {

    private Service review;
    private Service movie;
    private Service customer;

    @Data
    public static class Service {
        private String baseUrl;
    }
}
