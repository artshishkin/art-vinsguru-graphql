package net.shyshkin.study.graphql.graphqlplayground.lec11.config;

import graphql.schema.TypeResolver;
import net.shyshkin.study.graphql.graphqlplayground.lec11.dto.FruitDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class TypeResolverConfig {

    @Bean
    RuntimeWiringConfigurer configurer() {
        return c -> c.type("Result", b -> b.typeResolver(typeResolver()));
    }

    @Bean
    TypeResolver typeResolver() {
        ClassNameTypeResolver resolver = new ClassNameTypeResolver();
        resolver.addMapping(FruitDto.class, "Fruit");
        return resolver;
    }

}
