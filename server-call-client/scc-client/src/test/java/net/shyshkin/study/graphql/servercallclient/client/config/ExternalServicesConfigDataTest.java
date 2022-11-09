package net.shyshkin.study.graphql.servercallclient.client.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExternalServicesConfigDataTest {

    @Autowired
    ExternalServicesConfigData configData;

    @Test
    void readConfigsTest() {

        assertThat(configData.getMovie().getBaseUrl()).isEqualTo("http://localhost:8081/movie");
        assertThat(configData.getReview().getBaseUrl()).isEqualTo("http://localhost:8081/review");
        assertThat(configData.getCustomer().getBaseUrl()).isEqualTo("http://localhost:8081/customer");
    }
}