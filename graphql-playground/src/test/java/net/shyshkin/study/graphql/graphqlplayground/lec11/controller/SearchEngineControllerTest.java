package net.shyshkin.study.graphql.graphqlplayground.lec11.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.AbstractGraphQLSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("lec11")
class SearchEngineControllerTest extends AbstractGraphQLSpringBootTest {

    @Test
    void union_shouldReturnOneOfUnionComponents() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/unionTest")
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
                .documentName(docLocation + "/unionTest")
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
                .documentName(docLocation + "/unionTest")
                .operationName("SearchUnionWithTypename")
                .execute();

        //then
        response.path("search[*].name")
                .entityList(String.class)
                .contains("iPhone XS", "banana", "Lord of Rings");
    }

}