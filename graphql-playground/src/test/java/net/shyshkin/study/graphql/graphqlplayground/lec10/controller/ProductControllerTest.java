package net.shyshkin.study.graphql.graphqlplayground.lec10.controller;

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
        properties = {"spring.graphql.schema.locations: classpath:graphql/lec10"}
)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("lec10")
class ProductControllerTest {

    private static final String DOC_LOCATION = "lec10/";

    @Autowired
    GraphQlTester graphQlTester;

    @Test
    void interfaceQuery_shouldReturnOnlyInterfacesFields() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "interfaceTest")
                .operationName("GetProductInterface")
                .execute();

        //then
        response.path("products[0].id").hasValue()
                .path("products[0].price").matchesJson("10")
                .path("products[0].description").matchesJson("\"banana\"")
                .path("products[0].expiryDate").pathDoesNotExist();

        response.path("products[1].id").hasValue()
                .path("products[1].price").matchesJson("1000")
                .path("products[1].description").matchesJson("\"iPhone XS\"")
                .path("products[1].brand").pathDoesNotExist();

        response.path("products[2].id").hasValue()
                .path("products[2].price").matchesJson("100")
                .path("products[2].description").matchesJson("\"Lord of Rings\"")
                .path("products[2].author").pathDoesNotExist();
    }

    @Test
    void fruitQuery_shouldAddExpiryDateToResponse() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "interfaceTest")
                .operationName("GetProductsWithFruitExtensions")
                .execute();

        //then
        response.path("products[*].id").hasValue()
                .path("products[*].price").matchesJson("[10,1000,100]")
                .path("products[*].description").matchesJson("[\"banana\",\"iPhone XS\",\"Lord of Rings\"]")
                .path("products[0].expiryDate").hasValue()
                .path("products[*].brand").pathDoesNotExist()
                .path("products[*].author").pathDoesNotExist();
    }

    @Test
    void allFieldsQuery_shouldReturnFullResponse() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "interfaceTest")
                .operationName("GetProductsWithAllFields")
                .execute();

        //then
        response.path("products[*].id").hasValue()
                .path("products[*].price").matchesJson("[10,1000,100]")
                .path("products[*].description").matchesJson("[\"banana\",\"iPhone XS\",\"Lord of Rings\"]")
                .path("products[0].expiryDate").hasValue()
                .path("products[1].brand").matchesJson("\"APPLE\"")
                .path("products[2].author").matchesJson("\"Tolkien\"");
    }

    @Test
    void getTypenameTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "interfaceTest")
                .operationName("getTypename")
                .execute();

        //then
        response.path("products[*].id").hasValue()
                .path("products[*].__typename").matchesJson("[\"Fruit\",\"Electronics\",\"Book\"]")
                .path("products[0].expiryDate").hasValue();
    }

    @Test
    void getTypenameWithAliasTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(DOC_LOCATION + "interfaceTest")
                .operationName("getTypenameWithAlias")
                .execute();

        //then
        response.path("products[*].id").hasValue()
                .path("products[*].typ").matchesJson("[\"Fruit\",\"Electronics\",\"Book\"]")
                .path("products[*].__typename").pathDoesNotExist()
                .path("products[0].expiryDate").hasValue();
    }

}