package net.shyshkin.study.graphql.graphqlplayground.lec05_01.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Account;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Address;
import net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto.Customer;
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
@ActiveProfiles("lec05_01")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec05_01"}
)
class CustomerControllerTest {

    private static final String DOC_LOCATION = "lec05_01/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void getCustomersTest_shouldFetchAllTheData() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getCustomersTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );

        for (int i = 0; i < 5; i++) {
            response.path("customers[" + i + "].address")
                    .entity(Address.class)
                    .satisfies(addr -> assertThat(addr).hasNoNullFieldsOrProperties())
                    .satisfies(addr -> log.debug("{}", addr));
            response.path("customers[" + i + "].account")
                    .entity(Account.class)
                    .satisfies(acc -> assertThat(acc).hasNoNullFieldsOrProperties())
                    .satisfies(acc -> log.debug("{}", acc));
        }
    }


}