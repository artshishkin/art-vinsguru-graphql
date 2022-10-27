package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import net.shyshkin.study.graphql.errorhandling.lec15.service.MonitorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

class RequestInterceptorTest extends GraphQLAbstractTest {

    @Value("${app.caller-id-header}")
    private String callerIdHeaderName;

    @SpyBean
    MonitorService monitorService;

    @Test
    @DisplayName("Query customers WITHOUT header `caller-id` should populate EMPTY field into GraphQL execution Context")
    void absentCallerIdHeaderTest() {

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
        then(monitorService).should().call(eq(""));
    }

    @Test
    @DisplayName("Query customers WITH header `caller-id` should populate CORRECT field into GraphQL execution Context")
    void presentCallerIdHeaderTest() {

        //given
        String callerId = "art_id";
        HttpGraphQlTester testerWithHeader = graphQlTester.mutate()
                .header(callerIdHeaderName, callerId)
                .build();

        //when
        GraphQlTester.Response response = testerWithHeader
                .documentName(DOC_LOCATION + "crud")
                .operationName("AllCustomers")
                .execute();

        //then
        response.path("customers")
                .entityList(CustomerDto.class)
                .hasSize(4)
                .satisfies(customers -> assertThat(customers)
                        .allSatisfy(c -> assertThat(c).hasNoNullFieldsOrProperties()));
        then(monitorService).should().call(eq(callerId));
    }

}