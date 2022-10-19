package net.shyshkin.study.graphql.graphqlplayground.lec02.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.AgeRangeFilter;
import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lec02")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec02"}
)
class CustomerControllerTest {

    private static final String DOC_LOCATION = "lec02/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void getAllCustomersTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getAllCustomersTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(c.getCity()).contains("City"),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
    }

    @Test
    void getCustomerByIdTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getCustomerByIdTest")
                .variable("id", "3")
                .execute();

        //then
        response.path("customerById")
                .entity(Customer.class)
                .satisfies(c -> assertAll(
                                () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                () -> assertThat(c.getId()).isEqualTo(3),
                                () -> assertThat(c.getCity()).contains("City03"),
                                () -> assertThat(c.getName()).contains("Customer_03"),
                                () -> assertThat(c.getAge()).isBetween(18, 60),
                                () -> log.debug("{}", c)
                        )
                );
    }

    @Test
    void getCustomerByIdTest_JSON() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getCustomerByIdTest")
                .variable("id", "3")
                .execute();

        //then
        response.path("customerById.id").matchesJson("\"3\"");
        response.path("customerById.city").matchesJson("\"City03\"");
        response.path("customerById.name").matchesJson("\"Customer_03\"");
        response.path("customerById.age").entity(Integer.class).satisfies(age -> assertThat(age).isBetween(18, 60));
    }

    @Test
    void filterCustomersByNameTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "filterCustomersByNameTest")
                .variable("name", "04")
                .execute();

        //then
        response.path("customerNameContains")
                .entityList(Customer.class)
                .hasSize(1)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(c.getId()).isEqualTo(4),
                                        () -> assertThat(c.getCity()).isEqualTo("City04"),
                                        () -> assertThat(c.getName()).contains("Customer_04"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
    }

    @Test
    void multipleFieldsTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "multipleFieldsTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(c.getCity()).contains("City"),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
        response.path("customerById")
                .entity(Customer.class)
                .satisfies(c -> assertAll(
                                () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                () -> assertThat(c.getId()).isEqualTo(2),
                                () -> assertThat(c.getCity()).contains("City02"),
                                () -> assertThat(c.getName()).contains("Customer_02"),
                                () -> assertThat(c.getAge()).isBetween(18, 60),
                                () -> log.debug("{}", c)
                        )
                );
        response.path("customerNameContains")
                .entityList(Customer.class)
                .hasSize(1)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(c.getId()).isEqualTo(3),
                                        () -> assertThat(c.getCity()).isEqualTo("City03"),
                                        () -> assertThat(c.getName()).contains("Customer_03"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
    }

    @Test
    void filterCustomersByAgeRangeTest() {

        //given
        Integer minAge = 30;
        Integer maxAge = 55;

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "filterCustomersByAgeRangeTest")
                .execute();

        //then
        response.path("customersByAgeRange")
                .entityList(Customer.class)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(c.getAge()).isBetween(minAge, maxAge),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
    }

    @Test
    void filterCustomersByAgeRangeTest_param() {

        //given
        Integer minAge = 30;
        Integer maxAge = 55;
        AgeRangeFilter ageRangeFilter = AgeRangeFilter.builder().minAge(minAge).maxAge(maxAge).build();

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "filterCustomersByAgeRangePTest")
                .variable("filter", ageRangeFilter)
                .execute();

        //then
        response.path("customersByAgeRange")
                .entityList(Customer.class)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(c.getAge()).isBetween(minAge, maxAge),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
    }

}