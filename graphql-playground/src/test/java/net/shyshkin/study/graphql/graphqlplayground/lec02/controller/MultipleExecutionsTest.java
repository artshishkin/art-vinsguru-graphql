package net.shyshkin.study.graphql.graphqlplayground.lec02.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lec02")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec02"}
)
class MultipleExecutionsTest {

    private static final String DOC_LOCATION = "lec02/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void multipleCustomersByIdTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "multipleCustomersById")
                .execute();

        //then
        response.path("customer1").hasValue();
        response.path("customer1.id").matchesJson("\"1\"");
        response.path("customer1.city").matchesJson("\"City01\"");
        response.path("customer1.name").matchesJson("\"Customer_01\"");
        response.path("customer1.age").entity(Integer.class).satisfies(age -> assertThat(age).isBetween(18, 60));

        response.path("customer2").hasValue();
        response.path("customer2.id").matchesJson("\"2\"");
        response.path("customer2.city").matchesJson("\"City02\"");
        response.path("customer2.name").matchesJson("\"Customer_02\"");
        response.path("customer2.age").entity(Integer.class).satisfies(age -> assertThat(age).isBetween(18, 60));
    }

}