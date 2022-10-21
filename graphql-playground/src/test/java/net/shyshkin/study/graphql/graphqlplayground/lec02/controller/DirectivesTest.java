package net.shyshkin.study.graphql.graphqlplayground.lec02.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec02.service.CustomerService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lec02")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec02"}
)
class DirectivesTest {

    private static final String DOC_LOCATION = "lec02/";

    @Autowired
    GraphQlTester graphQlTester;

    @SpyBean
    CustomerService customerService;

    @Nested
    class DirectiveIncludeTests {

        @Test
        void directiveInclude_default_Test() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "directivesTest")
                    .operationName("CustomerByAgeRangeOperation")
                    .execute();

            //then
            response.path("cLow").hasValue();
        }

        @Test
        void directiveInclude_false_Test() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "directivesTest")
                    .operationName("CustomerByAgeRangeOperation")
                    .variable("includeLow", false)
                    .execute();

            //then
            response.path("cLow").pathDoesNotExist();
        }

        @Test
        void directiveInclude_true_Test() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "directivesTest")
                    .operationName("CustomerByAgeRangeOperation")
                    .variable("includeLow", true)
                    .execute();

            //then
            response.path("cLow").hasValue();
        }

    }

    @Nested
    class DirectiveSkipTests {

        @Test
        void directiveSkip_default_Test() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "directivesTest")
                    .operationName("CustomerByAgeRangeOperation")
                    .execute();

            //then
            response.path("cHigh").hasValue();
        }

        @Test
        void directiveSkip_false_Test() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "directivesTest")
                    .operationName("CustomerByAgeRangeOperation")
                    .variable("skipHigh", false)
                    .execute();

            //then
            response.path("cHigh").hasValue();
        }

        @Test
        void directiveSkip_true_Test() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "directivesTest")
                    .operationName("CustomerByAgeRangeOperation")
                    .variable("skipHigh", true)
                    .execute();

            //then
            response.path("cHigh").pathDoesNotExist();
        }
    }
}