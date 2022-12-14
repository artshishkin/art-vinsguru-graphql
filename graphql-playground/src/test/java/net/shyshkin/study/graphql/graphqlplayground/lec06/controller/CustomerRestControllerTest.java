package net.shyshkin.study.graphql.graphqlplayground.lec06.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec06.dto.CustomerWithOrdersDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lec06")
class CustomerRestControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void getCustomersThroughRest_shouldCallOrderServiceInParallelWithCustomersService() {

        //when
        webTestClient.get()
                .uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(CustomerWithOrdersDto.class)
                .hasSize(5)
                .value(customers -> assertThat(customers)
                        .allSatisfy(c -> assertThat(c)
                                .hasNoNullFieldsOrProperties()));
    }

}