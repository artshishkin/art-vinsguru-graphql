package net.shyshkin.study.graphql.graphqlplayground.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@Slf4j
@GraphQlTest
class HelloWorldControllerTest {

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

    }

    @Nested
    class WithExternalDocumentTests {

        @Test
        void helloWorld() {

            //when
            GraphQlTester.Response response = graphQlTester.documentName("sayHelloDoc")
                    .execute();

            //then
            response.path("sayHello")
                    .entity(String.class)
                    .isEqualTo("Hello World!");
        }

    }


}