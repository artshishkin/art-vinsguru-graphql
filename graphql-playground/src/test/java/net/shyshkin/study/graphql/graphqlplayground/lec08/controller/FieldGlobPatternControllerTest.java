package net.shyshkin.study.graphql.graphqlplayground.lec08.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@GraphQlTest(
        value = FieldGlobPatternController.class
)
@ActiveProfiles("lec08")
class FieldGlobPatternControllerTest {

    @Value("${lec}")
    private String docLocation;

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void fieldGlobPatternControllerTest() {

        graphQlTester
                .documentName(docLocation + "/field-glob-pattern")
                .executeAndVerify();
    }


}