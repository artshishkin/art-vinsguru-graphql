package net.shyshkin.study.graphql.servercallclient.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.server.rsocket")
public class RSocketServerConfigData {

    private String setupRoute;
    private Loadbalancer loadbalancer;

    @Data
    public static class Loadbalancer {

        private StaticAddressesLB staticAddressesLB;
        private DiscoveryServiceLB discoveryServiceLB;

        @Data
        public static class StaticAddressesLB {

            private List<ServiceInstanceAddress> instances;

            @Data
            public static class ServiceInstanceAddress {
                private String host = "localhost";
                private Integer port = 7000;
            }
        }

        @Data
        public static class DiscoveryServiceLB {
            private String serviceName;
            private String rsocketPortMetadataField = "rsocketPort";
        }

    }

}
