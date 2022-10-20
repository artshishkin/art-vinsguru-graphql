package net.shyshkin.study.graphql.graphqlplayground.lec04.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec04.dto.CustomerOrderDto;
import net.shyshkin.study.graphql.graphqlplayground.lec04.service.CustomerService;
import net.shyshkin.study.graphql.graphqlplayground.lec04.service.OrderService;
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
@ActiveProfiles("lec04")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec04"}
)
class CustomerControllerTest {

    private static final String DOC_LOCATION = "lec04/";

    @Autowired
    GraphQlTester graphQlTester;

    @SpyBean
    CustomerService customerService;

    @SpyBean
    OrderService orderService;

    @Test
    void getCustomersWithOrdersTest_shouldCallOrderService() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getCustomersWithOrdersTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c.getCity()).isNull(),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
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
                .hasSize(0);
        response.path("customers[2].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[3].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(0);
        response.path("customers[4].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));

        then(customerService).should().getAllCustomers();
        then(orderService).should(times(1)).ordersByCustomerIds(any());
        then(orderService).should(never()).ordersByCustomerId(any());
    }

    @Test
    void getCustomersWithOrdersMapTest_shouldCallOrderService() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getCustomersWithOrdersMapTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c.getCity()).isNull(),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
        response.path("customers[0].ordersMap")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[1].ordersMap")
                .entityList(CustomerOrderDto.class)
                .hasSize(0);
        response.path("customers[2].ordersMap")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[3].ordersMap")
                .entityList(CustomerOrderDto.class)
                .hasSize(0);
        response.path("customers[4].ordersMap")
                .entityList(CustomerOrderDto.class)
                .hasSize(5)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));

        then(customerService).should().getAllCustomers();
        then(orderService).should(times(1)).fetchOrdersAsMap(any());
        then(orderService).should(never()).ordersByCustomerId(any());
    }

    @Test
    void getCustomersOnlyTest_should_NOT_CallOrderService() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "getCustomersOnlyTest")
                .execute();

        //then
        response.path("customers")
                .entityList(Customer.class)
                .hasSize(5)
                .satisfies(list -> assertThat(list)
                        .allSatisfy(c -> assertAll(
                                        () -> assertThat(c.getCity()).isNull(),
                                        () -> assertThat(c.getName()).contains("Customer"),
                                        () -> assertThat(c.getAge()).isBetween(18, 60),
                                        () -> log.debug("{}", c)
                                )
                        )
                );
        response.path("customers[0].orders").pathDoesNotExist();

        then(customerService).should().getAllCustomers();
        then(orderService).shouldHaveNoInteractions();
    }

}