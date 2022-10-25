package net.shyshkin.study.graphql.crud.lec14.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.crud.lec14.dto.*;
import net.shyshkin.study.graphql.crud.lec14.service.CustomerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.graphql.schema.locations: classpath:graphql/lec14"
        }
)
@AutoConfigureHttpGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("lec14")
class CustomerEventControllerTest {

    private static final String DOC_LOCATION = "lec14/";

    @Autowired
    GraphQlTester graphQlTester;

    @LocalServerPort
    private Integer serverPort;

    WebSocketGraphQlTester webSocketGraphQlTester;

    @SpyBean
    CustomerService customerService;

    @BeforeEach
    void setUp() {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        String url = "http://localhost:" + serverPort + "/graphql";

        webSocketGraphQlTester = WebSocketGraphQlTester.builder(url, client).build();
    }

    @Test
    @DisplayName("Query customers should return all customers")
    @Order(10)
    void customersTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "subscription")
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
                .documentName(DOC_LOCATION + "subscription")
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
                .documentName(DOC_LOCATION + "subscription")
                .operationName("CustomerById")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("customerById").valueIsNull();
    }

    @Test
    @DisplayName("CREATED: Subscription for CustomerEvents should receive CREATED event during Mutation CreateCustomer")
    @Order(30)
    void createCustomerTest() {

        //given
        Map<String, Object> customerInput = Map.of(
                "name", "Tetyana",
                "age", 59,
                "city", "Warsaw"
        );
        Flux<CustomerEvent> customerEvents = subscriptionFlux();

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "subscription")
                .operationName("CreateCustomer")
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("createCustomer.id").hasValue();
        response.path("createCustomer.name").matchesJson("\"Tetyana\"");
        response.path("createCustomer.age").matchesJson("59");
        response.path("createCustomer.city").matchesJson("\"Warsaw\"");

        StepVerifier.create(customerEvents.take(1))
                .consumeNextWith(event -> assertThat(event)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("action", Action.CREATED)
                        .hasFieldOrPropertyWithValue("customer.name", "Tetyana")
                        .hasFieldOrPropertyWithValue("customer.age", 59)
                        .hasFieldOrPropertyWithValue("customer.city", "Warsaw")
                        .satisfies(ev -> log.debug("{}", ev))
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("UPDATED: Subscription for CustomerEvents should receive UPDATED event during Mutation UpdateCustomer if customer exists")
    @Order(40)
    void updateCustomer_present_Test() {

        //given
        Integer customerId = 3;
        Map<String, Object> customerInput = Map.of(
                "name", "Arina",
                "age", 12,
                "city", "London"
        );
        Flux<CustomerEvent> customerEvents = subscriptionFlux();

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "subscription")
                .operationName("UpdateCustomer")
                .variable("customerId", customerId)
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("updateCustomer.id").matchesJson("\"" + customerId + "\"");
        response.path("updateCustomer.name").matchesJson("\"Arina\"");
        response.path("updateCustomer.age").matchesJson("12");
        response.path("updateCustomer.city").matchesJson("\"London\"");

        StepVerifier.create(customerEvents.take(1))
                .consumeNextWith(event -> assertThat(event)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("action", Action.UPDATED)
                        .hasFieldOrPropertyWithValue("customer.id", customerId)
                        .hasFieldOrPropertyWithValue("customer.name", "Arina")
                        .hasFieldOrPropertyWithValue("customer.age", 12)
                        .hasFieldOrPropertyWithValue("customer.city", "London")
                        .satisfies(ev -> log.debug("{}", ev))
                )
                .verifyComplete();
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
                .documentName(DOC_LOCATION + "subscription")
                .operationName("UpdateCustomer")
                .variable("customerId", customerId)
                .variable("customerInput", customerInput)
                .execute();

        //then
        response.path("updateCustomer").valueIsNull();
    }

    @Test
    @DisplayName("DELETED: Subscription for CustomerEvents should receive DELETED event during Mutation DeleteCustomer if customer exists")
    @Order(50)
    void deleteCustomer_present_Test() {

        //given
        Integer customerId = 4;
        Flux<CustomerEvent> customerEvents = subscriptionFlux();

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "subscription")
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
        StepVerifier.create(customerEvents.take(1))
                .consumeNextWith(event -> assertThat(event)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("action", Action.DELETED)
                        .hasFieldOrPropertyWithValue("customer.id", customerId)
                        .hasFieldOrPropertyWithValue("customer.name", null)
                        .hasFieldOrPropertyWithValue("customer.age", null)
                        .hasFieldOrPropertyWithValue("customer.city", null)
                        .satisfies(ev -> log.debug("{}", ev))
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Mutation DeleteCustomer should return response with SUCCESS too status if customer absent")
    @Order(55)
    void deleteCustomer_absent_Test() {

        //given
        Integer customerId = 444;
        Flux<CustomerEvent> customerEvents = subscriptionFlux();

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "subscription")
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
        StepVerifier.create(customerEvents.take(1))
                .consumeNextWith(event -> assertThat(event)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("action", Action.DELETED)
                        .hasFieldOrPropertyWithValue("customer.id", customerId)
                        .hasFieldOrPropertyWithValue("customer.name", null)
                        .hasFieldOrPropertyWithValue("customer.age", null)
                        .hasFieldOrPropertyWithValue("customer.city", null)
                        .satisfies(ev -> log.debug("{}", ev))
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Multiple Mutations should produce multiple events")
    @Order(60)
    void multipleMutationsExecutionTest() {

        //given
        Map<String, Object> customerInput = Map.of(
                "name", "Boris",
                "age", 58,
                "city", "London"
        );
        Flux<CustomerEvent> customerEvents = subscriptionFlux();

        //when
        graphQlTester
                .documentName(DOC_LOCATION + "subscription")
                .operationName("MultipleMutations")
                .variable("newCustomer", customerInput)
                .executeAndVerify();

        //then
        StepVerifier.create(customerEvents.take(2))
                .consumeNextWith(event -> assertThat(event)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("action", Action.CREATED)
                        .hasFieldOrProperty("customer.id")
                        .hasFieldOrPropertyWithValue("customer.name", "Boris")
                        .hasFieldOrPropertyWithValue("customer.age", 58)
                        .hasFieldOrPropertyWithValue("customer.city", "London")
                        .satisfies(ev -> log.debug("{}", ev))
                )
                .consumeNextWith(event -> assertThat(event)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("action", Action.UPDATED)
                        .hasFieldOrPropertyWithValue("customer.id", 3)
                        .hasFieldOrPropertyWithValue("customer.name", "Anton")
                        .hasFieldOrPropertyWithValue("customer.age", 44)
                        .hasFieldOrPropertyWithValue("customer.city", "Las Vegas")
                        .satisfies(ev -> log.debug("{}", ev))
                )
                .verifyComplete();
    }

    private Flux<CustomerEvent> subscriptionFlux() {
        GraphQlTester.Subscription customerEventsSub = webSocketGraphQlTester
                .documentName(DOC_LOCATION + "subscription")
                .operationName("CustomerEventsSub")
                .executeSubscription();

        return customerEventsSub.toFlux("customerEvents", CustomerEvent.class);
    }

}