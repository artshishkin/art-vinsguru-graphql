package net.shyshkin.study.graphql.graphqlplayground.lec07.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec07.dto.CustomerOrderDto;
import net.shyshkin.study.graphql.graphqlplayground.lec07.dto.CustomerWithOrdersDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lec07")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec07"}
)
class CustomerOrderDataFetcherTest {

    private static final String DOC_LOCATION = "lec07/";

    @Autowired
    GraphQlTester graphQlTester;

    @SpyBean
    CustomerService customerService;

    @SpyBean
    OrderService orderService;

    @Test
    void getCustomersWithOrdersTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "customers")
                .operationName("WithOrders")
                .execute();

        //then
        response.path("customers")
                .entityList(CustomerWithOrdersDto.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c.getCity()).isNull(),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> assertThat(c.getOrders()).isNotNull(),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
        response.path("customers[0].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[1].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[4].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(0);

        then(customerService).should().getAllCustomers();
        then(orderService).should(times(5)).ordersByCustomerId(any());
    }

    @Test
    void getCustomersWithoutOrdersTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "customers")
                .operationName("WithoutOrders")
                .execute();

        //then
        response.path("customers")
                .entityList(CustomerWithOrdersDto.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c.getCity()).isNull(),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> assertThat(c.getOrders()).isNull(),
                                        () -> log.debug("{}", c)
                                )
                        )
                );

        then(customerService).should().getAllCustomers();
        then(orderService).should(never()).ordersByCustomerId(any());
    }

    @Test
    void dataFetcherConfig_yearTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "data-fetcher")
                .operationName("Year")
                .execute();

        //then
        response.path("year")
                .entity(Integer.class)
                .isEqualTo(2022);

        then(customerService).shouldHaveNoInteractions();
        then(orderService).shouldHaveNoInteractions();
    }

    @Test
    void dataFetcherConfig_authorTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "data-fetcher")
                .operationName("Author")
                .execute();

        //then
        response.path("author")
                .entity(String.class)
                .isEqualTo("Art");

        then(customerService).shouldHaveNoInteractions();
        then(orderService).shouldHaveNoInteractions();
    }

}