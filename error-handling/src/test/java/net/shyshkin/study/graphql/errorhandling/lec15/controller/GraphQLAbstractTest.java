package net.shyshkin.study.graphql.errorhandling.lec15.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

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
public abstract class GraphQLAbstractTest {

    protected static final String DOC_LOCATION = "lec15/";

    @Autowired
    HttpGraphQlTester graphQlTester;

}