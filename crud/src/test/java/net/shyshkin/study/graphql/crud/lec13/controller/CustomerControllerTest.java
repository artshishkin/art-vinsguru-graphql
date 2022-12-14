package net.shyshkin.study.graphql.crud.lec13.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.crud.lec13.dto.CustomerDto;
import net.shyshkin.study.graphql.crud.lec13.dto.DeleteResultDto;
import net.shyshkin.study.graphql.crud.lec13.dto.Status;
import net.shyshkin.study.graphql.crud.lec13.service.CustomerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.mutation.delay: 100ms"
        }
)
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("lec13")
class CustomerControllerTest {

    @Value("${lec}")
    private String docLocation;

    @Autowired
    GraphQlTester graphQlTester;

    @SpyBean
    CustomerService customerService;

    @Test
    @DisplayName("Query customers should return all customers")
    @Order(10)
    void customersTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("AllCustomers")
                .execute();

        //then
        response.path("customers")
                .entityList(CustomerDto.class)
                .hasSize(4)
                .satisfies(customers -> assertThat(customers)
                        .allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties()));
    }

    @ParameterizedTest
    @DisplayName("Query customer by id should return correct customer if customer exists")
    @Order(20)
    @ValueSource(ints = {1, 2, 3, 4})
    void customerById_present_Test(Integer customerId) {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("CustomerById")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("customerById.id").matchesJson("\"" + customerId + "\"");
    }

    @Test
    @DisplayName("Query customer by id should return NULL if customer does not exist")
    @Order(20)
    void customerById_absent_Test() {

        //given
        Integer customerId = 123;

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("CustomerById")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("customerById").valueIsNull();
    }

    @Test
    @DisplayName("Mutation CreateCustomer should create new customer")
    @Order(30)
    void createCustomerTest() {

        //given
        Map<String, Object> customerInput = Map.of(
                "name", "Tetyana",
                "age", 59,
                "city", "Warsaw"
        );

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("CreateCustomer")
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("createCustomer.id").hasValue();
        response.path("createCustomer.name").matchesJson("\"Tetyana\"");
        response.path("createCustomer.age").matchesJson("59");
        response.path("createCustomer.city").matchesJson("\"Warsaw\"");
    }

    @Test
    @DisplayName("Mutation UpdateCustomer should update customer if customer exists")
    @Order(40)
    void updateCustomer_present_Test() {

        //given
        Integer customerId = 3;
        Map<String, Object> customerInput = Map.of(
                "name", "Arina",
                "age", 12,
                "city", "London"
        );

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("UpdateCustomer")
                .variable("customerId", customerId)
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("updateCustomer.id").matchesJson("\"" + customerId + "\"");
        response.path("updateCustomer.name").matchesJson("\"Arina\"");
        response.path("updateCustomer.age").matchesJson("12");
        response.path("updateCustomer.city").matchesJson("\"London\"");
    }

    @Test
    @DisplayName("Mutation UpdateCustomer should return NULL customer if customer absent")
    @Order(40)
    void updateCustomer_absent_Test() {

        //given
        Integer customerId = 300;
        Map<String, Object> customerInput = Map.of(
                "name", "Arina",
                "age", 12,
                "city", "London"
        );

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("UpdateCustomer")
                .variable("customerId", customerId)
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("updateCustomer").valueIsNull();
    }

    @Test
    @DisplayName("Mutation DeleteCustomer should return response with SUCCESS status if customer present")
    @Order(50)
    void deleteCustomer_present_Test() {

        //given
        Integer customerId = 4;

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("DeleteCustomer")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("deleteCustomer")
                .entity(DeleteResultDto.class)
                .satisfies(result -> assertThat(result)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", customerId)
                        .hasFieldOrPropertyWithValue("status", Status.SUCCESS)
                );
    }

    @Test
    @DisplayName("Mutation DeleteCustomer should return response with SUCCESS too status if customer absent")
    @Order(50)
    void deleteCustomer_absent_Test() {

        //given
        Integer customerId = 444;

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("DeleteCustomer")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("deleteCustomer")
                .entity(DeleteResultDto.class)
                .satisfies(result -> assertThat(result)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", customerId)
                        .hasFieldOrPropertyWithValue("status", Status.SUCCESS)
                );
    }

    @Test
    @DisplayName("Multiple Mutations should execute sequentially")
    @Order(60)
    void multipleMutationsExecutionTest() {

        //given
        Map<String, Object> customerInput = Map.of(
                "name", "Boris",
                "age", 58,
                "city", "London"
        );
        InOrder inOrder = Mockito.inOrder(customerService);

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/crud")
                .operationName("MultipleMutations")
                .variable("newCustomer", customerInput)
                .execute();

        //then
        response
                .path("createCustomer.id").hasValue()
                .path("createCustomer.name").matchesJson("\"Boris\"")
                .path("createCustomer.age").matchesJson("58")
                .path("createCustomer.city").matchesJson("\"London\"");
        response
                .path("updateCustomer.id").matchesJson("\"3\"")
                .path("updateCustomer.name").matchesJson("\"Anton\"")
                .path("updateCustomer.age").matchesJson("44")
                .path("updateCustomer.city").matchesJson("\"Las Vegas\"");

        inOrder.verify(customerService).createCustomer(any());
        inOrder.verify(customerService).updateCustomer(eq(3), any());
    }

}