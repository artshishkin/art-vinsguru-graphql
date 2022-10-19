package net.shyshkin.study.graphql.graphqlplayground.lec01.controller;

import graphql.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@GraphQlTest(
        value = HelloWorldController.class,
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec01"}
)
@ActiveProfiles("lec01")
class HelloWorldControllerTest {

    private static final String DOC_LOCATION = "lec01/";

    @Autowired
    private GraphQlTester graphQlTester;

    @Nested
    class WithInlinedDocumentTests {

        @Test
        void helloWorld() {

            //given
            String document = "{sayHello}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("sayHello")
                    .entity(String.class)
                    .isEqualTo("Hello World!");
        }

        @Test
        void sayHelloTo() {

            //given
            String document = "{sayHelloTo(name:\"Art\")}";

            //when
            GraphQlTester.Response response = graphQlTester.document(document)
                    .execute();

            //then
            response.path("sayHelloTo")
                    .entity(String.class)
                    .isEqualTo("Hello Art!");
        }

    }

    @Nested
    class WithExternalDocumentTests {

        @Test
        void helloWorld() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "sayHelloDoc")
                    .execute();

            //then
            response.path("sayHello")
                    .entity(String.class)
                    .isEqualTo("Hello World!");
        }

        @Test
        void sayHelloTo() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "sayHelloToDoc")
                    .execute();

            //then
            response.path("sayHelloTo")
                    .entity(String.class)
                    .isEqualTo("Hello Art!");
        }

        @Test
        void sayHelloTo_throughParam() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "sayHelloToParamDoc")
                    .variable("helloName", "Art")
                    .execute();

            //then
            response.path("sayHelloTo")
                    .entity(String.class)
                    .isEqualTo("Hello Art!");
        }

    }

    @Nested
    class SchemaValidationTests {

        @Test
        @DisplayName("Calling sayHelloTo without argument `name` should return error of ValidationError")
        void sayHelloTo() {

            //given
            String query = "{sayHelloTo}";

            //when
            GraphQlTester.Response response = graphQlTester.document(query)
                    .execute();
            //then
            response.errors()
                    .satisfy(errors -> assertThat(errors)
                            .hasSize(1)
                            .allSatisfy(error -> assertAll(
                                    () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.ValidationError),
                                    () -> assertThat(error.getMessage()).isEqualTo("Validation error of type MissingFieldArgument: Missing field argument name @ 'sayHelloTo'"),
                                    () -> assertThat(error.getMessage()).contains("Missing field argument name"))
                            )
                    );
        }
    }

    @Nested
    class MultipleQueriesTests {

        @Test
        @DisplayName("Querying all the fields should return all the values in single response")
        void multiQuery() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName(DOC_LOCATION + "multiQueryDoc")
                    .execute();

            //then
            response.path("sayHello")
                    .entity(String.class)
                    .isEqualTo("Hello World!")
                    .path("random")
                    .entity(Integer.class)
                    .satisfies(rnd -> assertThat(rnd).isBetween(1, 100))
                    .path("sayHelloTo")
                    .entity(String.class)
                    .isEqualTo("Hello Kate!");
        }
    }


}