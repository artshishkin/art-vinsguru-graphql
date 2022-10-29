package net.shyshkin.study.graphql.graphqlplayground.lec09.config;

import graphql.schema.GraphQLScalarType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.List;

import static graphql.scalars.ExtendedScalars.Object;
import static graphql.scalars.ExtendedScalars.*;

@Configuration
@RequiredArgsConstructor
public class ScalarConfig {

    @Bean
    public RuntimeWiringConfigurer configurer() {

        List<GraphQLScalarType> qlScalarTypes = List.of(
                UUID,
                GraphQLLong,
                Date,
                DateTime,
                GraphQLBigDecimal,
                GraphQLBigInteger,
                GraphQLByte,
                GraphQLShort,
                LocalTime,
                Object
        );
        return c -> qlScalarTypes.forEach(c::scalar);
    }

}
