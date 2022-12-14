package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.HamcrestCondition.matching;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;

class ExceptionExampleControllerTest extends GraphQLAbstractTest{

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
//                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getMessage()).startsWith("Some Weird Issue"),
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
//                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getMessage()).startsWith("Some Weird Issue"),
                                        () -> assertThat(error.getPath()).isEqualTo("a"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR)
                                ),
                                Index.atIndex(0)
                        )
                        .satisfies(error -> assertAll(
//                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getMessage()).startsWith("Some Weird Issue"),
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
//                                        () -> assertThat(error.getMessage()).startsWith("INTERNAL_ERROR"),
                                        () -> assertThat(error.getMessage()).startsWith("Some Weird Issue"),
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

    @Test
    @DisplayName("When exception that we handle is thrown then error message should match exception message")
    void customHandledExceptionTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "exceptions")
                .operationName("AppCustom")
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(1)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).isEqualTo("App Custom Weird Issue"),
                                        () -> assertThat(error.getPath()).isEqualTo("appCustomException"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR)
                                ),
                                Index.atIndex(0)
                        )
                )
                .path("appCustomException").valueIsNull();
    }

    @Test
    @DisplayName("Resolving error with extensions ")
    void extensionsTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "exceptions")
                .operationName("AppCustom")
                .execute();

        //then
        response.errors()
                .satisfy(list -> assertThat(list)
                        .hasSize(1)
                        .satisfies(error -> assertAll(
                                        () -> assertThat(error.getMessage()).isEqualTo("App Custom Weird Issue"),
                                        () -> assertThat(error.getPath()).isEqualTo("appCustomException"),
                                        () -> assertThat(error.getErrorType()).isEqualTo(ErrorType.INTERNAL_ERROR),
                                        () -> assertThat(error.getExtensions())
                                                .hasEntrySatisfying("customerId", matching(is(123)))
                                                .hasEntrySatisfying("foo", matching(is("bar")))
                                                .hasEntrySatisfying("timestamp", matching(lessThanOrEqualTo(LocalDateTime.now().toString())))
                                ),
                                Index.atIndex(0)
                        )
                )
                .path("appCustomException").valueIsNull();
    }

}