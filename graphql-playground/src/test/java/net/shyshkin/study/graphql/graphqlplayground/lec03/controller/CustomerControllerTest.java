package net.shyshkin.study.graphql.graphqlplayground.lec03.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.AbstractGraphQLSpringBootTest;
import net.shyshkin.study.graphql.graphqlplayground.lec03.dto.Customer;
import net.shyshkin.study.graphql.graphqlplayground.lec03.dto.CustomerOrderDto;
import net.shyshkin.study.graphql.graphqlplayground.lec03.service.CustomerService;
import net.shyshkin.study.graphql.graphqlplayground.lec03.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Slf4j
@ActiveProfiles("lec03")
class CustomerControllerTest extends AbstractGraphQLSpringBootTest {

    @SpyBean
    CustomerService customerService;

    @SpyBean
    OrderService orderService;

    @Test
    void getCustomersWithOrdersTest_shouldCallOrderService() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/getCustomersWithOrdersTest")
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
    void getCustomersOnlyTest_should_NOT_CallOrderService() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/getCustomersOnlyTest")
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

    @Test
    void getCustomersLimitOrdersTest_shouldCallOrderService() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/getCustomersLimitOrdersTest")
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
                .hasSize(3)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[1].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(3)
                .satisfies(orders -> assertThat(orders)
                        .allSatisfy(o -> assertThat(o).hasNoNullFieldsOrProperties()));
        response.path("customers[4].orders")
                .entityList(CustomerOrderDto.class)
                .hasSize(0);

        then(customerService).should().getAllCustomers();
        then(orderService).should(times(5)).ordersByCustomerId(any());
    }

}