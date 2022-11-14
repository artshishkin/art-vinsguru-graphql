package net.shyshkin.study.graphql.servercallclient.client.config;

import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Slf4j
@Configuration
@Profile("service-discovery")
@RequiredArgsConstructor
public class ServiceDiscoveryLoadBalancedTargetConfig {

    private final RSocketServerConfigData configData;
    private final ReactiveDiscoveryClient reactiveDiscoveryClient;

    @Bean
    public Flux<List<LoadbalanceTarget>> serviceDiscoveryTargetsFlux() {

        return reactiveDiscoveryClient.getInstances(configData.getLoadbalancer().getDiscoveryServiceLB().getServiceName())
                .map(si -> LoadbalanceTarget.from(key(si), transport(si)))
                .collectList()
                .repeatWhen(f -> f.delayElements(Duration.ofMillis(500)));
    }

    private ClientTransport transport(ServiceInstance serviceInstance) {
        return TcpClientTransport.create(serviceInstance.getHost(), rsocketPort(serviceInstance));
    }

    private String key(ServiceInstance serviceInstance) {
        return serviceInstance.getHost() + ":" + rsocketPort(serviceInstance);
    }

    private Integer rsocketPort(ServiceInstance serviceInstance) {
        String rsocketPort = serviceInstance.getMetadata()
                .getOrDefault(configData.getLoadbalancer().getDiscoveryServiceLB().getRsocketPortMetadataField(), "7000");
        return Integer.parseInt(rsocketPort);
    }

}
