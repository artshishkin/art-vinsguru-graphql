package net.shyshkin.study.graphql.movieapp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public abstract class BaseTest {

    static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/art-vinsguru-graphql-external-services")
            .withExposedPorts(7070);

    @DynamicPropertySource
    static void externalServicesProperties(DynamicPropertyRegistry registry) {
        externalServices.start();
        registry.add("app.service.review.base-url", () -> serviceUrl("review"));
        registry.add("app.service.movie.base-url", () -> serviceUrl("movie"));
        registry.add("app.service.customer.base-url", () -> serviceUrl("customer"));
    }

    private static String serviceUrl(String serviceName) {
        return String.format("http://%s:%d/%s",
                externalServices.getHost(),
                externalServices.getMappedPort(7070),
                serviceName);
    }

}