package net.shyshkin.study.graphql.graphqlplayground.lec12.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.AbstractGraphQLSpringBootTest;
import net.shyshkin.study.graphql.graphqlplayground.lec12.service.CacheMonitorFakeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@Slf4j
@TestPropertySource(properties = {"logging.level.ROOT: debug"})
@ActiveProfiles("lec12")
class PlayWithCachingControllerTest extends AbstractGraphQLSpringBootTest {

    @SpyBean
    CacheMonitorFakeService cacheMonitorFakeService;

    @Test
    @DirtiesContext
    @DisplayName("Executing simple query twice should not parse and validate query twice")
    void simpleQueryTest() {

        //first call
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/cachingTest")
                .operationName("Simple")
                .execute();

        response.path("sayHello").matchesJson("\"Hello Art!\"");
        then(cacheMonitorFakeService).should().confirmParsingAndValidation();

        //second call
        response = graphQlTester
                .documentName(docLocation + "/cachingTest")
                .operationName("Simple")
                .execute();
        response.path("sayHello").matchesJson("\"Hello Art!\"");
        then(cacheMonitorFakeService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Executing simple query twice with different names would parse and validate query twice")
    void simpleQueryWithDifferentNamesTest() {

        //first call
        GraphQlTester.Response response = graphQlTester
                .document("{sayHello(name: \"Art\")}")
                .execute();

        response.path("sayHello").matchesJson("\"Hello Art!\"");

        //second call
        response = graphQlTester
                .document("{sayHello(name: \"Arina\")}")
                .execute();
        response.path("sayHello").matchesJson("\"Hello Arina!\"");
        then(cacheMonitorFakeService).should(times(2)).confirmParsingAndValidation();
    }

    @Test
    @DirtiesContext
    @DisplayName("Executing full correct query with variables twice with different names should NOT parse and validate query twice")
    void fullCorrectQueryTest() {

        //first call
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/cachingTest")
                .operationName("Correct")
                .variable("name", "Art")
                .execute();

        response.path("sayHello").matchesJson("\"Hello Art!\"");
        then(cacheMonitorFakeService).should().confirmParsingAndValidation();

        //second call
        response = graphQlTester
                .documentName(docLocation + "/cachingTest")
                .operationName("Correct")
                .variable("name", "Arina")
                .execute();
        response.path("sayHello").matchesJson("\"Hello Arina!\"");
        then(cacheMonitorFakeService).shouldHaveNoMoreInteractions();
    }

}