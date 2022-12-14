package net.shyshkin.study.graphql.graphqlplayground.lec09.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.AbstractGraphQLSpringBootTest;
import net.shyshkin.study.graphql.graphqlplayground.lec09.dto.AllTypes;
import net.shyshkin.study.graphql.graphqlplayground.lec09.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.HamcrestCondition.matching;
import static org.hamcrest.CoreMatchers.is;

@Slf4j
@ActiveProfiles("lec09")
@AutoConfigureHttpGraphQlTester
class ScalarControllerTest extends AbstractGraphQLSpringBootTest {

    @Test
    void allScalarTypesTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/all-types")
                .execute();

        //then
        response.path("get")
                .entity(AllTypes.class)
                .satisfies(types -> assertThat(types)
                        .hasNoNullFieldsOrProperties()
                        .satisfies(t -> log.debug("get: {}", t))
                );
    }

    @Test
    void objectScalarTest() {

        //when
        GraphQlTester.Response response = graphQlTester
                .documentName(docLocation + "/object")
                .execute();

        //then
        response.path("products[0]")
                .entity(Product.class)
                .satisfies(product -> assertThat(product)
                        .hasNoNullFieldsOrProperties()
                        .satisfies(p -> assertThat(p.getAttributes())
                                .hasEntrySatisfying("distance", matching(is("300km")))
                                .hasEntrySatisfying("cost", matching(is("10_000_000")))
                                .hasEntrySatisfying("height", matching(is("10km")))
                        )
                );

        response.path("products[1].name").matchesJson("\"Stugna\"");
        response.path("products[1].attributes.weight").matchesJson("\"20kg\"");
        response.path("products[1].attributes.nation").matchesJson("\"Ukraine\"");
        response.path("products[1].attributes.cost").matchesJson("\"20_000\"");

    }

}