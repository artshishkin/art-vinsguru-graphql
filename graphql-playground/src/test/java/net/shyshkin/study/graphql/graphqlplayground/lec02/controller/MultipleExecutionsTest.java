package net.shyshkin.study.graphql.graphqlplayground.lec02.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.AbstractGraphQLSpringBootTest;
import net.shyshkin.study.graphql.graphqlplayground.lec02.dto.AgeRangeFilter;
import net.shyshkin.study.graphql.graphqlplayground.lec02.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@Slf4j
@ActiveProfiles("lec02")
class MultipleExecutionsTest extends AbstractGraphQLSpringBootTest {

    @SpyBean
    CustomerService customerService;

    @Test
    void multipleCustomersByIdTest() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/multipleCustomersById")
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

    @Test
    void multipleFilteredByAgeTest() {

        //given
        AgeRangeFilter filterLow = AgeRangeFilter.builder()
                .minAge(18)
                .maxAge(35)
                .build();
        AgeRangeFilter filterHigh = AgeRangeFilter.builder()
                .minAge(40)
                .maxAge(60)
                .build();

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/multipleCustomersFiltered")
                .execute();

        //then
        response.path("cLow").hasValue();
        response.path("cHigh").hasValue();
        then(customerService).should().getCustomersWithinAge(eq(filterLow));
        then(customerService).should().getCustomersWithinAge(eq(filterHigh));
    }

    @Test
    void fragmentsTest() {

        //given
        AgeRangeFilter filterLow = AgeRangeFilter.builder()
                .minAge(18)
                .maxAge(35)
                .build();
        AgeRangeFilter filterHigh = AgeRangeFilter.builder()
                .minAge(40)
                .maxAge(60)
                .build();

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/fragmentsTest")
                .execute();

        //then
        response.path("cLow").hasValue();
        response.path("cHigh").hasValue();
        then(customerService).should().getCustomersWithinAge(eq(filterLow));
        then(customerService).should().getCustomersWithinAge(eq(filterHigh));
    }

    @Test
    void operations_1_Test() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/operationsTest")
                .operationName("CustomerByAgeRangeOperation")
                .execute();

        //then
        response.path("cLow").hasValue();
        response.path("cHigh").hasValue();
        then(customerService).should(times(2)).getCustomersWithinAge(any());
        response.path("customerById").pathDoesNotExist();
        then(customerService).should(never()).getCustomerById(any());
    }

    @Test
    void operations_2_Test() {

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/operationsTest")
                .operationName("CustomerByIdOperation")
                .execute();

        //then
        response.path("customerById").hasValue();
        then(customerService).should().getCustomerById(eq(1));
        response.path("cLow").pathDoesNotExist();
        response.path("cHigh").pathDoesNotExist();
        then(customerService).should(never()).getCustomersWithinAge(any());
    }

    @Test
    void variablesTest() {

        //given
        Integer customerId = 2;

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/variablesTest")
                .operationName("CustomerByIdOperation")
                .variable("customerId", customerId)
                .execute();

        //then
        response.path("customerById").hasValue();
        response.path("customerById.id").matchesJson("\"" + customerId + "\"");
        then(customerService).should().getCustomerById(eq(customerId));
    }

    @Test
    void variablesWithDefaultValueTest() {

        //given
        Integer defaultId = 1;

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/variablesTest")
                .operationName("CustomerByIdOperation")
                .execute();

        //then
        response.path("customerById").hasValue();
        response.path("customerById.id").matchesJson("\"" + defaultId + "\"");
        then(customerService).should().getCustomerById(eq(defaultId));
    }

    @Test
    void variablesAssignmentTest() {

        //given
        AgeRangeFilter filterLow = AgeRangeFilter.builder().minAge(5).maxAge(55).build();
        AgeRangeFilter defaultFilterHigh = AgeRangeFilter.builder().minAge(40).maxAge(60).build();

        //when
        GraphQlTester.Response response = graphQlTester.documentName(docLocation + "/variablesTest")
                .operationName("CustomerByAgeRangeOperation")
                .variable("filterL", filterLow)
                .execute();

        //then
        response.path("cLow").hasValue();
        response.path("cHigh").hasValue();
        then(customerService).should().getCustomersWithinAge(eq(filterLow));
        then(customerService).should().getCustomersWithinAge(eq(defaultFilterHigh));
    }


}