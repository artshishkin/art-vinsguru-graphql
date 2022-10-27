package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles({"header-check-filter"})
class HeaderCheckFilterTest extends GraphQLAbstractTest {

    @Value("${app.client-id-header}")
    private String clientIdHeaderName;

    @Value("${spring.graphql.path}")
    private String graphqlMapping;

    @Autowired
    WebTestClient webTestClient;

    @Value("graphql-test/lec15/crud.graphql")
    private ClassPathResource queryResource;

    @Test
    @DisplayName("Query customers WITH header `client-id` should return CORRECT result")
    void presentClientIdHeaderTest() {

        //given
        String clientId = "super_client_id";
        HttpGraphQlTester testerWithHeader = graphQlTester.mutate()
                .header(clientIdHeaderName, clientId)
                .build();

        //when
        GraphQlTester.Response response = testerWithHeader
                .documentName(DOC_LOCATION + "crud")
                .operationName("AllCustomers")
                .execute();

        //then
        response.path("customers")
                .entityList(CustomerDto.class)
                .hasSize(4)
                .satisfies(customers -> assertThat(customers)
                        .allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties()));
    }

    @Test
    @DisplayName("Query customers through WebClient POST request WITH header `client-id` should return CORRECT result")
    void presentClientIdHeaderWebClientTest() {

        //given
        String clientId = "super_client_id";
        String queryContent = asString(queryResource);
        Map<String, String> requestBody = Map.of(
                "query", queryContent,
                "operationName", "AllCustomers"
        );

        //when
        webTestClient.post()
                .uri(graphqlMapping)
                .header(clientIdHeaderName, clientId)
                .bodyValue(requestBody)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.customers[0].id").isEqualTo("1")
                .jsonPath("$.data.customers[1].name").isEqualTo("Kate")
                .jsonPath("$.data.customers[2].age").isEqualTo(12)
                .jsonPath("$.data.customers[3].city").isEqualTo("Krakiv");
    }

    @Test
    @DisplayName("Query customers through WebClient POST request WITHOUT header `client-id` should return BAD_REQUEST HttpStatus code")
    void absentClientIdHeaderWebClientTest() {

        //given
        String clientId = "super_client_id";
        String queryContent = asString(queryResource);
        Map<String, String> requestBody = Map.of(
                "query", queryContent,
                "operationName", "AllCustomers"
        );

        //when
        webTestClient.post()
                .uri(graphqlMapping)
                .bodyValue(requestBody)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody().isEmpty();
    }

    private static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}