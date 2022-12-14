package net.shyshkin.study.graphql.graphqlplayground.lec05.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.AbstractGraphQLSpringBootTest;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Account;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.AccountType;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Address;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@ActiveProfiles("lec05")
class CustomerControllerTest extends AbstractGraphQLSpringBootTest {

    @Test
    void getCustomersTest_shouldFetchAllTheData() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/getCustomersTest")
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

    @Test
    void fieldAliasTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/fieldAliasTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5);

        for (int i = 0; i < 5; i++) {
            response.path("customers[" + i + "].AD").hasValue(); //alias for address
            response.path("customers[" + i + "].ac").hasValue(); //alias for account
            response.path("customers[" + i + "].ac.balance")
                    .entity(Integer.class)
                    .satisfies(amount -> assertThat(amount).isBetween(0, 1000));
            response.path("customers[" + i + "].ac.type")
                    .entity(AccountType.class)
                    .satisfies(accountType -> assertThat(accountType).isNotNull());
        }
    }

}