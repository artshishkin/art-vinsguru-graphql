package net.shyshkin.study.graphql.servercallclient.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.server.rsocket")
public class RSocketServerConfigData {
    private String host = "localhost";
    private Integer port = 7000;
    private String setupRoute;
}
