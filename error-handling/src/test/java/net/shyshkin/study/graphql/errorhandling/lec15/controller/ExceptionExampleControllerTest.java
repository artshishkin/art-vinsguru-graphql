package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.graphql.schema.locations: classpath:graphql/lec15",
                "app.mutation.delay: 10ms"
        }
)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("lec15")
class ExceptionExampleControllerTest {


    private static final String DOC_LOCATION = "lec15/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    @DisplayName("When exception is thrown during query then errors field should be present")
    void unhandledExceptionTest() {

        //given
//{
//  "errors": [
//      {
//          "message": "INTERNAL_ERROR for 07940656-184",
//          "locations": [
//              {
//                  "line": 2,
//                  "column": 5
//              }
//          ],
//          "path": [
//              "unhandledException"
//          ],
//          "extensions": {
//              "classification": "INTERNAL_ERROR"
//          }
//      }
//  ],
//  "data": {
//      "unhandledException": null
//  }
//}

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "exceptions")
                .operationName("Unhandled")
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(1)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getPath()).isEqualTo("unhandledException"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR)
                                ),
                                Index.atIndex(0)
                        )
                )
                .path("unhandledException").valueIsNull();
    }

    @Test
    @DisplayName("When MANY exceptions are thrown during query then All the errors field should be present")
    void multipleUnhandledExceptionTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "exceptions")
                .operationName("MultiUnhandled")
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(2)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getPath()).isEqualTo("a"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR)
                                ),
                                Index.atIndex(0)
                        )
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getPath()).isEqualTo("b"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR)
                                ),
                                Index.atIndex(1)
                        )
                )
                .path("a").valueIsNull()
                .path("b").valueIsNull();
    }

    @Test
    @DisplayName("When exception is thrown along with normal response during single query then the error field AND normal response should be present")
    void mixingUnhandledErrorWithNormalResponseTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "exceptions")
                .operationName("MixingUnhandledErrorWithNormalResponse")
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(1)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getPath()).isEqualTo("unhandledException"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR)
                                ),
                                Index.atIndex(0)
                        )
                )
                .path("customerById").hasValue()
                .path("customerById.id").matchesJson("\"1\"")
                .path("customerById.name").matchesJson("\"Art\"")
                .path("customerById.age").matchesJson("39")
                .path("customerById.city").matchesJson("\"Volodymyr\"");
    }

}