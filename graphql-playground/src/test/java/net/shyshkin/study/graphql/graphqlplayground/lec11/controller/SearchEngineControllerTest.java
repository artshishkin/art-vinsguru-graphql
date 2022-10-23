package net.shyshkin.study.graphql.graphqlplayground.lec11.controller;

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
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec11"}
)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("lec11")
class SearchEngineControllerTest {

    private static final String DOC_LOCATION = "lec11/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void union_shouldReturnOneOfUnionComponents() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "unionTest")
                .operationName("SearchUnion")
                .execute();

        //then
        response.path("search[*].id").hasValue()
                .path("search[*].price").matchesJson("[1000]")
                .path("search[*].description").hasValue()
                .path("search[*].expiryDate").hasValue()
                .path("search[*].brand").matchesJson("[\"APPLE\"]")
                .path("search[*].title").matchesJson("[\"Lord of Rings\"]")
                .path("search[*].author").matchesJson("[\"Tolkien\"]");
    }

    @Test
    void unionWithTypename_shouldReturnAllTypes() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "unionTest")
                .operationName("SearchUnionWithTypename")
                .execute();

        //then
        response.path("search[*].__typename")
                .entityList(String.class)
                .contains("Fruit", "Electronics", "Book");
    }

    @Test
    void unionWithAliases_shouldReturnAlias() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "unionTest")
                .operationName("SearchUnionWithTypename")
                .execute();

        //then
        response.path("search[*].name")
                .entityList(String.class)
                .contains("iPhone XS", "banana", "Lord of Rings");
    }

}