package net.shyshkin.study.graphql.errorhandling.lec15.config;

import graphql.schema.TypeResolver;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class TypeResolverConfig {

    @Bean
    RuntimeWiringConfigurer configurer() {
        return c -> c.type("CustomerResponse", b -> b.typeResolver(typeResolver()));
    }

    @Bean
    TypeResolver typeResolver() {
        ClassNameTypeResolver resolver = new ClassNameTypeResolver();
        resolver.addMapping(CustomerDto.class, "Customer");
        return resolver;
    }

}
