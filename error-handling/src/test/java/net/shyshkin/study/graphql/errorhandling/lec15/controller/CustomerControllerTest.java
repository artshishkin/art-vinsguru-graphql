package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.DeleteResultDto;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.Status;
import net.shyshkin.study.graphql.errorhandling.lec15.service.CustomerService;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.HamcrestCondition.matching;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.graphql.schema.locations: classpath:graphql/lec15",
                "app.mutation.delay: 10ms"
        }
)
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("lec15")
class CustomerControllerTest {

    private static final String DOC_LOCATION = "lec15/";

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

    @ParameterizedTest
    @DisplayName("Query customer by id should return correct customer if customer exists")
    @Order(20)
    @ValueSource(ints = {1, 2, 3, 4})
    void customerById_present_Test(Integer customerId) {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "crud")
                .operationName("CustomerById")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("customerById.id").matchesJson("\"" + customerId + "\"");
    }

    @Test
    @DisplayName("Query customer by id should return ERROR if customer does not exist")
    @Order(20)
    void customerById_absent_Test() {

        //given
        Integer customerId = 123;

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "crud")
                .operationName("CustomerById")
                .variable("customerId", customerId)
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(1)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).isEqualTo("No such customer"),
                                        () -> assertThat(error.getPath()).isEqualTo("customerById"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
                                        () -> assertThat(error.getExtensions())
                                                .hasEntrySatisfying("customerId", matching(is(customerId)))
                                ),
                                Index.atIndex(0)
                        )
                )
                .path("customerById").valueIsNull();
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
                .documentName(DOC_LOCATION + "crud")
                .operationName("CreateCustomer")
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("createCustomer.id").hasValue()
                .path("createCustomer.name").matchesJson("\"Tetyana\"")
                .path("createCustomer.age").matchesJson("59")
                .path("createCustomer.city").matchesJson("\"Warsaw\"");
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
                .documentName(DOC_LOCATION + "crud")
                .operationName("UpdateCustomer")
                .variable("customerId", customerId)
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("updateCustomer.id").matchesJson("\"" + customerId + "\"")
                .path("updateCustomer.name").matchesJson("\"Arina\"")
                .path("updateCustomer.age").matchesJson("12")
                .path("updateCustomer.city").matchesJson("\"London\"");
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
                .documentName(DOC_LOCATION + "crud")
                .operationName("UpdateCustomer")
                .variable("customerId", customerId)
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(1)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).isEqualTo("No such customer"),
                                        () -> assertThat(error.getPath()).isEqualTo("updateCustomer"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST),
                                        () -> assertThat(error.getExtensions())
                                                .hasEntrySatisfying("customerId", matching(is(customerId)))
                                ),
                                Index.atIndex(0)
                        )
                )
                .path("updateCustomer").valueIsNull();
    }

    @Test
    @DisplayName("Mutation DeleteCustomer should return response with SUCCESS status if customer present")
    @Order(50)
    void deleteCustomer_present_Test() {

        //given
        Integer customerId = 4;

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "crud")
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
    @DisplayName("Mutation DeleteCustomer should return response with FAILURE status if customer absent")
    @Order(50)
    void deleteCustomer_absent_Test() {

        //given
        Integer customerId = 444;

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "crud")
                .operationName("DeleteCustomer")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("deleteCustomer")
                .entity(DeleteResultDto.class)
                .satisfies(result -> assertThat(result)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", customerId)
                        .hasFieldOrPropertyWithValue("status", Status.FAILURE)
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
                .documentName(DOC_LOCATION + "crud")
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