package net.shyshkin.study.graphql.graphqlplayground.lec12.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.graphql.schema.locations: classpath:graphql/lec12",
                "logging.level.ROOT: debug"
        }
)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("lec12")
class PlayWithCachingControllerTest {

    private static final String DOC_LOCATION = "lec12/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void viewLogsTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "cachingTest")
                .execute();

        //then
        response.path("sayHello").matchesJson("\"Hello Art!\"");
    }

}