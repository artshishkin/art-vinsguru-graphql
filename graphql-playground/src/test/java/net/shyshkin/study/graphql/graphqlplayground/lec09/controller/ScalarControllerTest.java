package net.shyshkin.study.graphql.graphqlplayground.lec09.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec09.dto.AllTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lec09")
@AutoConfigureHttpGraphQlTester
@TestPropertySource(
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec09"}
)
class ScalarControllerTest {

    private static final String DOC_LOCATION = "lec09/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void fieldGlobPatternControllerTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "all-types")
                .execute();

        //then
        response.path("get")
                .entity(AllTypes.class)
                .satisfies(types -> assertThat(types)
                        .hasNoNullFieldsOrProperties()
                        .satisfies(t -> log.debug("get: {}", t))
                );
    }

}