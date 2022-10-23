package net.shyshkin.study.graphql.graphqlplayground.lec08.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@GraphQlTest(
        value = FieldGlobPatternController.class,
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec08"}
)
@ActiveProfiles("lec08")
class FieldGlobPatternControllerTest {

    private static final String DOC_LOCATION = "lec08/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void fieldGlobPatternControllerTest() {

        graphQlTester
                .documentName(DOC_LOCATION + "field-glob-pattern")
                .executeAndVerify();
    }


}